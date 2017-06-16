package com.dreamland.rxcache.policy;

import android.content.Context;

import com.dreamland.rxcache.ICacheLoader;
import com.dreamland.rxcache.RxCacheLoaderHelper;
import com.dreamland.rxcache.rxutils.Tuple3;

import rx.Observable;


/**
 * Created by XMD on 2017/6/16.
 */

public class LoadFromMemoryFirstPolicy<T> implements ILoaderPolicy<T> {
    @Override
    public Observable<T> load(Context context,String url, Class<T> clazz, ICacheLoader<T> cacheLoader){
        Tuple3<Context,String,Class<T>> tuple3 = new Tuple3<>(context,url,clazz);
        return cacheLoader.loadFromMemory(Observable.just(tuple3))
                .switchIfEmpty(cacheLoader.loadFromDisk(Observable.just(tuple3)))
                .switchIfEmpty(cacheLoader.loadFromNetwork(Observable.just(tuple3)))
                .switchIfEmpty(new RxCacheLoaderHelper.NoDataObservable<T>().create());
    }


}
