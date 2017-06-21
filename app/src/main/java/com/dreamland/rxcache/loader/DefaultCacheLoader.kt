package com.dreamland.rxcache.loader

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import com.bluelinelabs.logansquare.LoganSquare
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.network.RxHttp
import com.dreamland.rxcache.rxutils.Tuple3
import com.dreamland.rxcache.utils.ACache
import rx.Observable
import rx.functions.Func1
import java.io.IOException

/**
 * Created by XMD on 2017/6/21.
 */
open class DefaultCacheLoader(private var maxCacheCount: Int = DEFAULT_BUFFER_SIZE) : ICacheLoader {


    private var lruCache: LruCache<String, String>? = null
    private val lruCacheLock = Object()


    override fun setMaxMemoryCacheCount(maxCacheCount: Int): DefaultCacheLoader {
        this.maxCacheCount = maxCacheCount
        return this
    }

    override fun init(context: Context) {
        synchronized(lruCacheLock, block = {
            lruCache = LruCache(maxCacheCount)
        })
    }

    override fun clear(context: Context) {
        synchronized(lruCacheLock, block = {
            lruCache?.evictAll()
        })
        ACache.get(context).clear()
    }


    override fun <T> loadFromMemory(observable: Observable<Tuple3<Context, String, Class<T>>>) = observable
            .map(Func1<Tuple3<Context, String, Class<T>>, T> { t ->
                if (DEBUG) Log.i(TAG, "loadFromMemory:" + t._2)
                synchronized(lruCacheLock, block = {
                    try {
                        return@Func1 LoganSquare.parse(lruCache?.get(t._2), t._3)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e : NullPointerException){

                    }

                    return@Func1 null
                })
            }
            ).filter(emptyFilter())!!


    override fun <T> loadFromDisk(observable: Observable<Tuple3<Context, String, Class<T>>>) = observable
            .map(Func1<Tuple3<Context, String, Class<T>>, T> { t ->
                if (DEBUG) Log.i(TAG, "loadFromDisk:" + t._2)
                val cached = ACache.get(t._1).getAsString(t._2)
                if (!TextUtils.isEmpty(cached)) {
                    synchronized(lruCacheLock, block = {
                        lruCache?.put(t._2, cached)
                    })
                }
                try {
                    return@Func1 LoganSquare.parse(cached, t._3)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e : NullPointerException){

                }
                return@Func1 null
            }
            ).filter(emptyFilter())!!

    override fun <T> loadFromNetwork(observable: Observable<Tuple3<Context, String, Class<T>>>) = observable
            .filter { t -> t._2.startsWith("http") }
            .flatMap { t ->
                if (DEBUG) {
                    Log.i(TAG, "loadFromNetwork:" + t._2)
                }
                RxHttp.get(t._2)
                        .retry { integer, throwable ->
                            if (DEBUG) {
                                Log.i(TAG, "retry:" + integer + " : " + t._2 + " " + throwable.message)
                            }
                            integer <= 3 && (throwable.message == null || !throwable.message.toString().startsWith("Unable to resolve host"))
                        }.flatMap({ responseBody ->
                    Observable.just(responseBody)
                            .map { responseBody ->
                                try {
                                    val ret = responseBody.string()
                                    synchronized(lruCacheLock, block = {
                                        lruCache?.put(t._2, ret)
                                    })
                                    ACache.get(t._1).put(t._2, ret)
                                    return@map LoganSquare.parse(ret, t._3)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                } catch (e : NullPointerException){
                                    e.printStackTrace()
                                }
                                return@map null
                            }
                }, { null },
                        { Observable.empty<T>() })
            }.filter(emptyFilter())!!

    private fun <T> emptyFilter() = Func1<T, Boolean> { t -> t != null }

    companion object {
        private val TAG = "DefaultCacheLoader"
        private val DEBUG = true
        private val DEFAULT_CACHE_MAX_COUNT = 100
    }

}