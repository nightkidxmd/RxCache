package com.dreamland.rxcache.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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

    override fun setMaxMemoryCacheCount(maxCacheCount: Int): DefaultCacheLoader {
        this.maxCacheCount = maxCacheCount
        return this
    }

    override fun init(context: Context) {

    }

    override fun clear(context: Context) {
        ACache.get(context).clear()
    }


    override fun <T : Any> loadFromMemory(observable: Observable<Tuple3<Context, String, Class<T>>>) = loadFromDisk(observable)


    override fun <T : Any> loadFromDisk(observable: Observable<Tuple3<Context, String, Class<T>>>) = observable
            .map({ (_1, _2, _3) ->
                when (_3) {
                    Bitmap::class.java -> {
                        return@map ACache.get(_1).getAsBitmap(_2)
                    }
                    else -> {
                        try {
                            return@map LoganSquare.parse(ACache.get(_1).getAsString(_2), _3)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: NullPointerException) {
                            return@map null
                        }
                    }
                }
            }
            ).filter(emptyFilter())!!

    override fun <T : Any> loadFromNetwork(observable: Observable<Tuple3<Context, String, Class<T>>>) = observable
            .filter { t -> t._2.startsWith("http") }
            .flatMap { (_1, _2, _3) ->
                RxHttp.get(_2)
                        .retry { integer, throwable ->
                            if (DEBUG) {
                                Log.i(TAG, "retry:" + integer + " : " + _2 + " " + throwable.message)
                            }
                            integer <= 3 && (throwable.message == null || !throwable.message.toString().startsWith("Unable to resolve host"))
                        }.flatMap({ responseBody ->
                    Observable.just(responseBody)
                            .map { responseBody ->

                                when (_3) {
                                    Bitmap::class.java -> {
                                        val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                                        ACache.get(_1).put(_2, bitmap)
                                        return@map bitmap
                                    }
                                    else -> {
                                        try {
                                            val ret = responseBody.string()
                                            ACache.get(_1).put(_2, ret)
                                            return@map LoganSquare.parse(ret, _3)
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        } catch (e: NullPointerException) {
                                            return@map null
                                        }
                                    }
                                }
                            }
                },
                        { null },
                        { null }
                )
            }.filter(emptyFilter())!!

    private fun <T> emptyFilter() = Func1<T, Boolean> { t -> t != null && t !is Unit }

    companion object {
        private val TAG = "DefaultCacheLoader"
        private val DEBUG = true
        private val DEFAULT_CACHE_MAX_COUNT = 100
    }

}