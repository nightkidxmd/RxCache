package com.dreamland.rxcache.policy

import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.RxCacheLoaderHelper
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import java.net.URI

/**
 * Created by XMD on 2017/8/4.
 */
class LoadDiskOnlyPolicy: ILoaderPolicy {
    override fun <T : Any> load(tuple: Tuple4<Context, URI?, URI?, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader){
        loadFromDisk(Observable.just(tuple))
                .switchIfEmpty(loadDefault(Observable.just(tuple)))
                .switchIfEmpty(RxCacheLoaderHelper.NoDataObservable().create())!!
    }
}