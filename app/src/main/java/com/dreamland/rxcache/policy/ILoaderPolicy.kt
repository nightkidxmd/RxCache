package com.dreamland.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.rxutils.Tuple3
import rx.Observable

/**
 * Created by XMD on 2017/6/20.
 */
interface ILoaderPolicy {
    /**
     *
     * @param tuple3
     * @param iCacheLoader  custom [ICacheLoader]
     * @return
     */
    fun <T:Any> load(tuple3: Tuple3<Context, String, Class<T>>, iCacheLoader: ICacheLoader): Observable<in Any?>
}