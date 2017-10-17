package com.dreamland.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import java.net.URI

/**
 * Created by XMD on 2017/6/20.
 */
interface ILoaderPolicy {
    /**
     *
     * @param tuple
     * @param iCacheLoader  custom [ICacheLoader]
     * @return
     */
    fun <T:Any> load(tuple: Tuple4<Context, URI?,URI?, Class<T>>, iCacheLoader: ICacheLoader): Observable<in T?>
}