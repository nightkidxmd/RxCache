package com.dreamland.rxcache.policy;

import android.content.Context;

import com.dreamland.rxcache.ICacheLoader;
import com.dreamland.rxcache.RxCacheLoaderHelper;
import com.dreamland.rxcache.rxutils.Tuple3;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;


/**
 * Created by XMD on 2017/6/16.
 * loadFrom memory/disk/network at the same time.
 * the priority to pick the data is network > memory > disk
 */
public class LoadFromAllAtTheSameTimePolicy<T> implements ILoaderPolicy<T> {
    @Override
    public Observable<T> load(Context context, String url, Class<T> clazz, ICacheLoader<T> iCacheLoader) {
        Tuple3<Context, String, Class<T>> tuple3 = new Tuple3<>(context, url, clazz);
        return Observable.zip(
                iCacheLoader.loadFromMemory(Observable.just(tuple3)).switchIfEmpty(Observable.<T>just(null)),
                iCacheLoader.loadFromDisk(Observable.just(tuple3)).switchIfEmpty(Observable.<T>just(null)),
                iCacheLoader.loadFromNetwork(Observable.just(tuple3)).switchIfEmpty(Observable.<T>just(null)),
                new Func3<T, T, T, T>() {
                    @Override
                    public T call(T t, T t2, T t3) {
                        return (t3 != null ? t3 : (t != null ? t : t2));
                    }
                }
        )
                .filter(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        return t != null;
                    }
                })
                .switchIfEmpty(new RxCacheLoaderHelper.NoDataObservable<T>().create());
    }
}
