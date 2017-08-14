package com.dreamland.rxcache.policy
import android.content.Context
import com.dreamland.rxcache.ICacheLoader
import com.dreamland.rxcache.RxCacheLoaderHelper
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import rx.Subscriber
import java.net.URI

/**
 * Created by XMD on 2017/8/1.
 * 该次读取本地有效数据，并且同时去获取网络数据，更新到本地，供下次使用
 */
class LoadLocalAndUpdateFromNetwork : ILoaderPolicy {
    override fun <T : Any> load(tuple: Tuple4<Context, URI?, URI?, Class<T>>, iCacheLoader: ICacheLoader) = with(iCacheLoader) {
        Observable.concatDelayError(loadFromDisk(Observable.just(tuple))
                .switchIfEmpty(loadDefault(Observable.just(tuple))),
                loadFromNetwork(Observable.just(tuple)).flatMap (
                        //拦截信号，这里只是要load后保存，不通知客户
                        { Observable.create { s: Subscriber<in T> -> s.onCompleted() }},
                        {null},
                        {null})
        ).switchIfEmpty(RxCacheLoaderHelper.NoDataObservable().create())!!
    }
}