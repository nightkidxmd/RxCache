package com.dreamland.rxcache

import android.content.Context
import com.dreamland.rxcache.rxutils.Tuple4
import rx.Observable
import java.net.URI

/**
 * Created by XMD on 2017/6/20.
 */
interface ICacheLoader {
    fun setMaxMemoryCacheCount(maxCacheCount: kotlin.Int): ICacheLoader
    fun init(context: Context)
    fun clear(context: Context)
    fun <T : Any> loadFromMemory(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>): Observable<in Any?>
    fun <T : Any> loadFromDisk(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>): Observable<in Any?>
    fun <T : Any> loadFromNetwork(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>): Observable<in Any?>
    fun <T : Any> loadDefault(observable: Observable<Tuple4<Context, URI?, URI?, Class<T>>>): Observable<in Any?>
}