package com.tuyou.tsd.rxcache.loader

import android.content.Context
import com.dreamland.rxcache.loader.DefaultCacheLoader
import com.dreamland.rxcache.rxutils.Tuple3
import rx.Observable

/**
 * Created by XMD on 2017/6/21.
 */
class NoMemoryCacheLoader : DefaultCacheLoader() {
    override fun <T:Any> loadFromMemory(observable: Observable<Tuple3<Context, String, Class<T>>>): Observable<Any?> = Observable.empty()
}