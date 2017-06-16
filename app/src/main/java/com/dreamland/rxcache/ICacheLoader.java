package com.dreamland.rxcache;

import android.content.Context;

import com.dreamland.rxcache.rxutils.Tuple3;

import rx.Observable;

/**
 * Created by XMD on 2017/6/15.
 */

public interface ICacheLoader<T> {
     /**
      * set max memory cache count
      * @param maxCount
      */
     ICacheLoader<T> setMaxMemoryCacheCount(int maxCount);
     void init(Context context);
     void clear(Context context);
     Observable<T> loadFromMemory(Observable<Tuple3<Context, String, Class<T>>> observable);
     Observable<T> loadFromDisk(Observable<Tuple3<Context, String, Class<T>>> observable);
     Observable<T> loadFromNetwork(Observable<Tuple3<Context, String, Class<T>>> observable);
}
