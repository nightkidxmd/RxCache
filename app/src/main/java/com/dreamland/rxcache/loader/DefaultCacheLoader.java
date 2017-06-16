package com.dreamland.rxcache.loader;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dreamland.rxcache.ICacheLoader;
import com.dreamland.rxcache.network.RxHttp;
import com.dreamland.rxcache.rxutils.Tuple3;
import com.dreamland.rxcache.utils.ACache;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;


/**
 * Created by XMD on 2017/6/15.
 * Can only cache text config, not support image
 */
public class DefaultCacheLoader<T> implements ICacheLoader<T> {
    private static final String TAG = DefaultCacheLoader.class.getSimpleName();
    private static final int DEFAULT_CACHE_MAX_COUNT = 100;
    private static final boolean DEBUG = true;
    private LruCache<String, String> lruCache = null;
    private int maxSize = DEFAULT_CACHE_MAX_COUNT;
    private final Object lruCacheLock = new Object();

    public DefaultCacheLoader(int maxSize) {
        this.maxSize = maxSize;
    }

    public DefaultCacheLoader() {

    }


    @Override
    public DefaultCacheLoader<T> setMaxMemoryCacheCount(int maxCount) {
        this.maxSize = maxCount;
        synchronized (lruCacheLock) {
            if (lruCache != null) {
                lruCache.resize(maxCount);
            }
        }
        return this;
    }

    @Override
    public void init(Context context) {
        synchronized (lruCacheLock) {
            lruCache = new LruCache<>(maxSize);
        }
    }

    @Override
    public void clear(Context context) {
        if (lruCache != null) {
            lruCache.evictAll();
        }
        ACache.get(context).clear();
    }

    @Override
    public Observable<T> loadFromMemory(Observable<Tuple3<Context, String, Class<T>>> observable) {
        return observable.map(new Func1<Tuple3<Context, String, Class<T>>, T>() {
            @Override
            public T call(Tuple3<Context, String, Class<T>> contextStringClassTuple3) {
                if (DEBUG) {
                    Log.i(TAG, "loadFromMemory:" + contextStringClassTuple3._2);
                }
                try {
                    synchronized (lruCacheLock) {
                        String cached = lruCache != null ? lruCache.get(contextStringClassTuple3._2) : null;
                        return !TextUtils.isEmpty(cached) ?
                                LoganSquare.parse(cached, contextStringClassTuple3._3)
                                : null;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).filter(emptyFilter);
    }

    @Override
    public Observable<T> loadFromDisk(Observable<Tuple3<Context, String, Class<T>>> observable) {
        return observable.map(new Func1<Tuple3<Context, String, Class<T>>, T>() {
            @Override
            public T call(Tuple3<Context, String, Class<T>> contextStringClassTuple3) {
                if (DEBUG) {
                    Log.i(TAG, "loadFromDisk:" + contextStringClassTuple3._2);
                }
                try {
                    String ret = ACache.get(contextStringClassTuple3._1).getAsString(contextStringClassTuple3._2);
                    if (!TextUtils.isEmpty(ret)) {
                        synchronized (lruCacheLock) {
                            if (lruCache != null) {
                                lruCache.put(contextStringClassTuple3._2, ret);
                            }
                        }
                        return LoganSquare.parse(ret, contextStringClassTuple3._3);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).filter(emptyFilter);
    }

    @Override
    public Observable<T> loadFromNetwork(Observable<Tuple3<Context, String, Class<T>>> observable) {
        return observable.filter(new Func1<Tuple3<Context, String, Class<T>>, Boolean>() {
            @Override
            public Boolean call(Tuple3<Context, String, Class<T>> contextStringClassTuple3) {
                return contextStringClassTuple3._2.startsWith("http");
            }
        })
                .flatMap(new Func1<Tuple3<Context, String, Class<T>>, Observable<T>>() {
                    @Override
                    public Observable<T> call(final Tuple3<Context, String, Class<T>> contextStringClassTuple3) {
                        if (DEBUG) {
                            Log.i(TAG, "loadFromNetwork:" + contextStringClassTuple3._2);
                        }
                        return RxHttp.get(contextStringClassTuple3._2)
                                .retry(new Func2<Integer, Throwable, Boolean>() {
                                    @Override
                                    public Boolean call(Integer integer, Throwable throwable) {
                                        if (DEBUG) {
                                            Log.i(TAG, "retry:" + integer + " : " + contextStringClassTuple3._2 + " " + throwable.getMessage());
                                        }
                                        return integer <= 3 &&
                                                (throwable.getMessage() == null
                                                        || !throwable.getMessage().startsWith("Unable to resolve host"));
                                    }
                                }).flatMap(new Func1<ResponseBody, Observable<T>>() {
                                    @Override
                                    public Observable<T> call(ResponseBody responseBody) {
                                        return Observable.just(responseBody)
                                                .map(new Func1<ResponseBody, T>() {
                                                    @Override
                                                    public T call(ResponseBody responseBody) {
                                                        try {
                                                            String ret = responseBody.string();
                                                            synchronized (lruCacheLock) {
                                                                if (lruCache != null) {
                                                                    lruCache.put(contextStringClassTuple3._2, ret);
                                                                }
                                                            }
                                                            ACache.get(contextStringClassTuple3._1).put(contextStringClassTuple3._2, ret);
                                                            return LoganSquare.parse(ret, contextStringClassTuple3._3);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        return null;
                                                    }
                                                });
                                    }
                                }, new Func1<Throwable, Observable<? extends T>>() {
                                    @Override
                                    public Observable<? extends T> call(Throwable throwable) {
                                        return null;
                                    }
                                }, new Func0<Observable<? extends T>>() {
                                    @Override
                                    public Observable<? extends T> call() {
                                        return Observable.empty();
                                    }
                                });
                    }
                }).filter(emptyFilter);
    }

    private Func1<T, Boolean> emptyFilter = new Func1<T, Boolean>() {
        @Override
        public Boolean call(T t) {
            return t != null;
        }
    };
}
