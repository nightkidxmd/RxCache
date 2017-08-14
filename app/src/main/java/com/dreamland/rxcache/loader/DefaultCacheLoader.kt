package com.tuyou.tsd.rxcache.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.bluelinelabs.logansquare.LoganSquare
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.network.RxHttp
import com.dreamland.rxcache.rxutils.Tuple3
import com.dreamland.rxcache.rxutils.Tuple4
import com.dreamland.rxcache.utils.ACache
import rx.Observable
import rx.functions.Func1
import java.io.FileInputStream
import java.io.IOException
import java.net.URI


/**
 * Created by XMD on 2017/6/21.
 */
open class DefaultCacheLoader(private var maxCacheCount: Int = DEFAULT_BUFFER_SIZE) : ICacheLoader {

    override fun setMaxMemoryCacheCount(maxCacheCount: Int): DefaultCacheLoader {
        this.maxCacheCount = maxCacheCount
        return this
    }

    override fun init(context: Context) {
        ACache.get(context)
    }

    override fun clear(context: Context) {
        ACache.get(context).clear()
    }


    override fun <T : Any> loadFromMemory(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>) = loadFromDisk(observable)


    override fun <T : Any> loadFromDisk(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>) =
            _loadFromDisk(observable.flatMap { (_1,_2,_,_4) -> Observable.just(Tuple3(_1,_2,_4)) })

    override fun <T : Any> loadFromNetwork(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>) = observable
            .filter { (_, _2, _) ->
                when (_2?.scheme) {
                    "http", "https", "ftp" -> {
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            .flatMap { (_1, _2, _, _4) ->
                RxHttp.get(_2.toString())
                        .retry { integer, throwable ->
                            if (DEBUG) {
                                Log.i(TAG, "retry:$integer : $_2 ${throwable.message}")
                            }
                            integer <= 3 && (throwable.message == null || !throwable.message.toString().startsWith("Unable to resolve host"))
                        }.flatMap({ responseBody ->
                    Observable.just(responseBody)
                            .map { responseBody ->
                                when (_4) {
                                    Bitmap::class.java -> {
                                        val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                                        ACache.get(_1).put(_2.toString(), bitmap)
                                        return@map bitmap
                                    }
                                    else -> {
                                        try {
                                            val ret = responseBody.string()
                                            val obj = LoganSquare.parse(ret, _4)
                                            ACache.get(_1).put(_2.toString(), ret)
                                            return@map obj
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


    override fun <T : Any> loadDefault(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>) =
            _loadFromDisk(observable.flatMap { (_1,_,_3,_4) -> Observable.just(Tuple3(_1,_3,_4)) })

    private fun <T : Any> _loadFromDisk(observable: Observable<Tuple3<Context, URI?, Class<T>>>) =
            observable.map({ (_1, _2, _3) ->
        when (_3) {
            Bitmap::class.java -> {
                if (_2?.scheme?.equals("file") ?: false) {
                    return@map BitmapFactory.decodeFile(_2?.path)
                } else if (_2?.scheme?.equals("assets") ?: false) {
                    return@map BitmapFactory.decodeStream(_1.assets?.open(_2?.path?.substring(1)))
                } else {
                    return@map ACache.get(_1).getAsBitmap(_2.toString())
                }
            }
            else -> {
                try {
                    if (_2?.scheme?.equals("file") ?: false) {
                        return@map LoganSquare.parse(FileInputStream(_2?.path), _3)
                    }else if (_2?.scheme?.equals("assets") ?: false) {
                        return@map LoganSquare.parse(_1.assets?.open(_2?.path?.substring(1)), _3)
                    } else {
                        return@map LoganSquare.parse(ACache.get(_1).getAsString(_2.toString()), _3)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    return@map null
                }
            }
        }
    }
    ).filter(emptyFilter())!!


    private fun <T> emptyFilter() = Func1<T, Boolean> { t -> t != null && t !is Unit }

    companion object {
        private val TAG = "DefaultCacheLoader"
        private val DEBUG = true
        private val DEFAULT_CACHE_MAX_COUNT = 100
    }

}