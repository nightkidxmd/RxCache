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
class LoadFromNetworkFirstPolicy : ILoaderPolicy {
    override fun <T : Any> load(tuple: Tuple4<Context, URI?, URI?, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader) {
        loadFromNetwork(Observable.just(tuple))
                .switchIfEmpty(loadFromMemory(Observable.just(tuple)))
                .switchIfEmpty(loadFromDisk(Observable.just(tuple)))
                .switchIfEmpty(loadDefault(Observable.just(tuple)))
                .switchIfEmpty(RxCacheLoaderHelper.NoDataObservable().create())!!
    }
}