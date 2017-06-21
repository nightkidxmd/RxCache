package com.dreamland.rxcache

import android.content.Context
import com.dreamland.rxcache.rxutils.Tuple3
import com.dreamland.rxcache.loader.DefaultCacheLoader
import com.dreamland.rxcache.policy.ILoaderPolicy
import com.dreamland.rxcache.policy.LoadFromAllAtTheSameTimePolicy
import com.dreamland.rxcache.policy.LoadFromMemoryFirstPolicy
import com.tuyou.tsd.rxcache.policy.LoadFromNetworkFirstPolicy
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by XMD on 2017/6/21.
 */

object RxCacheLoaderHelper{
    var defaultCacheLoader: ICacheLoader = DefaultCacheLoader()
    var defaultCachePolicy: ILoaderPolicy = LoadFromMemoryFirstPolicy()
    fun init(context: Context){
        defaultCacheLoader.init(context)
    }

    fun clear(context: Context){
        defaultCacheLoader.clear(context)
    }

    fun clear(context: Context, iCacheLoader: ICacheLoader){
        iCacheLoader.clear(context)
    }

    //----------------------------------------------------------------------------------------------

    /**
     * @param context
     * @param url
     * @param clazz
     * @param iLoaderPolicy
     * @param iCacheLoader
     */

    fun <T> load(context: Context, url:String, clazz:Class<T>,
                 iLoaderPolicy: ILoaderPolicy = defaultCachePolicy,
                 iCacheLoader: ICacheLoader = defaultCacheLoader) =
            iLoaderPolicy.load(makeTuple(context,url,clazz),iCacheLoader).subscribeOn(Schedulers.io())!!




    /**
     *
     */
    fun <T> loadFromMemoryFirst(context: Context, url:String, clazz: Class<T>,
                                iCacheLoader: ICacheLoader = defaultCacheLoader)
    = load(context,url,clazz, LoadFromMemoryFirstPolicy(),iCacheLoader)

    /**
     * compatible with java
     */
    fun <T> loadFromMemoryFirst(context: Context, url:String, clazz: Class<T>)
            = load(context,url,clazz, LoadFromMemoryFirstPolicy())

    fun <T> loadFromNetworkFirst(context: Context, url:String, clazz: Class<T>,
                                 iCacheLoader: ICacheLoader = defaultCacheLoader)
    = load(context,url,clazz, LoadFromNetworkFirstPolicy(),iCacheLoader)

    /**
     * compatible with java
     */
    fun <T> loadFromNetworkFirst(context: Context, url:String, clazz: Class<T>)
            = load(context,url,clazz, LoadFromNetworkFirstPolicy())

    /**
     *
     */
    fun <T> loadFromAllAtTheSameTime(context: Context, url: String, clazz: Class<T>,
                                     iCacheLoader: ICacheLoader = defaultCacheLoader)
    = load(context,url,clazz, LoadFromAllAtTheSameTimePolicy(),iCacheLoader)

    /**
     * compatible with java
     */
    fun <T> loadFromAllAtTheSameTime(context: Context, url: String, clazz: Class<T>)
            = load(context,url,clazz, LoadFromAllAtTheSameTimePolicy())


    //----------------------------------------------------------------------------------------------
    @JvmField
    val ERROR_NO_DATA = "error_no_data"

    class NoDataObservable<T> {
        fun create(): Observable<T> {
            return Observable.create { subscriber -> subscriber.onError(Throwable(ERROR_NO_DATA)) }
        }
    }
    //----------------------------------------------------------------------------------------------

    private fun <T> makeTuple(context: Context, url:String, clazz:Class<T> ) = Tuple3(context,url,clazz)



}