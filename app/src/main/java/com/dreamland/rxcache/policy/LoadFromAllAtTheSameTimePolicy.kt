package com.dreamland.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.RxCacheLoaderHelper
import com.dreamland.rxcache.rxutils.Tuple3
import rx.Observable

/**
 * Created by XMD on 2017/6/20.
 */
class LoadFromAllAtTheSameTimePolicy : ILoaderPolicy {
    override fun <T:Any> load(tuple3: Tuple3<Context, String, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader) {
        Observable.zip(
                loadFromNetwork(Observable.just(tuple3)).switchIfEmpty(Observable.just(null)),
                loadFromDisk(Observable.just(tuple3)).switchIfEmpty(Observable.just(null)),
                loadFromMemory(Observable.just(tuple3)).switchIfEmpty(Observable.just(null)),
                { t1, t2, t3 ->
                    t1 ?: t3 ?: t2
                }
        )
                .filter { t -> t != null }
                .switchIfEmpty(RxCacheLoaderHelper.NoDataObservable<T>().create())!!
    }
}