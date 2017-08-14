package com.dreamland.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.RxCacheLoaderHelper
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import java.net.URI

/**
 * Created by XMD on 2017/6/20.
 */
class LoadFromAllAtTheSameTimePolicy : ILoaderPolicy {
    override fun <T:Any> load(tuple: Tuple4<Context, URI?, URI?, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader) {
        Observable.zip(
                loadFromNetwork(Observable.just(tuple)).switchIfEmpty(Observable.just(null)),
                loadFromDisk(Observable.just(tuple)).switchIfEmpty(Observable.just(null)),
                loadFromMemory(Observable.just(tuple)).switchIfEmpty(Observable.just(null)),
                { t1, t2, t3 ->
                    t1 ?: t3 ?: t2
                }
        )
                .filter { t -> t != null }
                .switchIfEmpty(loadDefault(Observable.just(tuple)))
                .switchIfEmpty(RxCacheLoaderHelper.NoDataObservable().create())!!
    }
}