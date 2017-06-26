package com.tuyou.tsd.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.RxCacheLoaderHelper
import com.dreamland.rxcache.policy.ILoaderPolicy

import com.dreamland.rxcache.rxutils.Tuple3
import rx.Observable

/**
 * Created by XMD on 2017/6/20.
 */
class LoadFromNetworkFirstPolicy: ILoaderPolicy {
    override fun <T:Any> load(tuple3: Tuple3<Context, String, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader){
            loadFromNetwork(Observable.just(tuple3))
            .switchIfEmpty(loadFromMemory(Observable.just(tuple3)))
            .switchIfEmpty(loadFromDisk(Observable.just(tuple3)))
            .switchIfEmpty(RxCacheLoaderHelper.NoDataObservable<T>().create())!!
    }
}