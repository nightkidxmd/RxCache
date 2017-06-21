package com.dreamland.rxcache

import android.content.Context
import com.dreamland.rxcache.rxutils.Tuple3
import rx.Observable

/**
 * Created by XMD on 2017/6/20.
 */
interface ICacheLoader {
    fun setMaxMemoryCacheCount(maxCacheCount: kotlin.Int): ICacheLoader
    fun init(context: Context)
    fun clear(context: Context)
    fun <T> loadFromMemory(observable: Observable<Tuple3<Context, String, Class<T>>>): Observable<T?>
    fun <T> loadFromDisk(observable: Observable<Tuple3<Context, String, Class<T>>>): Observable<T?>
    fun <T> loadFromNetwork(observable: Observable<Tuple3<Context, String, Class<T>>>): Observable<T?>
}