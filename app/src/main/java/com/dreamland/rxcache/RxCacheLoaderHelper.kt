package com.dreamland.rxcache

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.dreamland.rxcache.loader.DefaultCacheLoader
import com.dreamland.rxcache.policy.*
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.URI

/**
 * Created by XMD on 2017/6/21.
 */
object RxCacheLoaderHelper {
    const val SCHEME_FILE = "file"
    const val SCHEME_ANDROID_ASSET = "assets"
    const val SCHEME_HTTP = "http"

    var defaultCacheLoader: ICacheLoader = DefaultCacheLoader()
    var defaultCachePolicy: ILoaderPolicy = LoadFromMemoryFirstPolicy()
    fun init(context: Context) {
        defaultCacheLoader.init(context)
    }

    fun clear(context: Context) {
        defaultCacheLoader.clear(context)
    }

    fun clear(context: Context, iCacheLoader: ICacheLoader) {
        iCacheLoader.clear(context)
    }

    //----------------------------------------------------------------------------------------------


    /**
     * @param context
     * @param uri
     * @param clazz
     * @param iLoaderPolicy
     * @param iCacheLoader
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> load(context: Context, uri: URI?, defaultURI: URI? = null, clazz: Class<T>,
                       iLoaderPolicy: ILoaderPolicy = defaultCachePolicy,
                       iCacheLoader: ICacheLoader = defaultCacheLoader): Observable<T> =
            iLoaderPolicy.load(makeTuple(context, uri, defaultURI, clazz), iCacheLoader).subscribeOn(Schedulers.io())
                    .flatMap({ t -> Observable.just(t as T) })!!


    /**
     *
     */
    fun <T : Any> loadFromMemoryFirst(context: Context, uri: URI?, defaultURI: URI? = null, clazz: Class<T>,
                                      iCacheLoader: ICacheLoader = defaultCacheLoader)
            = load(context, uri, defaultURI, clazz, LoadFromMemoryFirstPolicy(), iCacheLoader)

    /**
     *
     */
    fun <T : Any> loadFromNetworkFirst(context: Context, uri: URI?, defaultURI: URI? = null, clazz: Class<T>,
                                       iCacheLoader: ICacheLoader = defaultCacheLoader)
            = load(context, uri, defaultURI, clazz, LoadFromNetworkFirstPolicy(), iCacheLoader)

    /**
     *
     */
    fun <T : Any> loadFromAllAtTheSameTime(context: Context, uri: URI?, defaultURI: URI? = null, clazz: Class<T>,
                                           iCacheLoader: ICacheLoader = defaultCacheLoader)
            = load(context, uri, defaultURI, clazz, LoadFromAllAtTheSameTimePolicy(), iCacheLoader)

    /**
     *
     */
    fun <T : Any> loadDiskOnly(context: Context, uri: URI?, defaultURI: URI? = null, clazz: Class<T>,
                               iCacheLoader: ICacheLoader = defaultCacheLoader)
            = load(context, uri, defaultURI, clazz, LoadDiskOnlyPolicy(), iCacheLoader)

    /**
     *
     *
     * load image async
     * @param context
     * @param uri
     * @param imageView  should added from main looper for imageView$post not useful
     * @param defaultImageRes
     *
     */
    fun loadImage(context: Context, uri: URI?, defaultURI: URI? = null, imageView: ImageView, defaultImageRes: Int)
            = with(imageView) {
        loadFromMemoryFirst(context, uri, defaultURI, Bitmap::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t -> setImageBitmap(t) },
                        { e ->
                            e.printStackTrace()
                            setImageResource(defaultImageRes)
                        },
                        { })
    }!!

    //----------------------------------------------------------------------------------------------
    @JvmField
    val ERROR_NO_DATA = "error_no_data"

    class NoDataObservable {
        fun <T> create(): Observable<T> =
                Observable.create { subscriber -> subscriber.onError(Throwable(ERROR_NO_DATA)) }
    }
    //----------------------------------------------------------------------------------------------

    private fun <T> makeTuple(context: Context, uri: URI?, defaultURI: URI?, clazz: Class<T>)
            = Tuple4(context, uri, defaultURI, clazz)
}