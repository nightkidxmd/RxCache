package com.dreamland.rxcache.policy;

import android.content.Context;

import com.dreamland.rxcache.ICacheLoader;

import rx.Observable;


/**
 * Created by XMD on 2017/6/15.
 */

public interface ILoaderPolicy<T> {
    /**
     *
     * @param context
     * @param url
     * @param clazz
     * @param iCacheLoader  custom {@link ICacheLoader}
     * @return
     */
    Observable<T> load(Context context, String url, Class<T> clazz, ICacheLoader<T> iCacheLoader);
}
