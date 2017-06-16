package com.dreamland.rxcache;

import android.content.Context;

import com.dreamland.rxcache.loader.DefaultCacheLoader;
import com.dreamland.rxcache.policy.ILoaderPolicy;
import com.dreamland.rxcache.policy.LoadFromAllAtTheSameTimePolicy;
import com.dreamland.rxcache.policy.LoadFromMemoryFirstPolicy;
import com.dreamland.rxcache.policy.LoadFromNetworkFirstPolicy;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by XMD on 2017/6/15.
 */
public class RxCacheLoaderHelper {
    public static final String ERROR_NO_DATA = "error_no_data";

    private static RxCacheLoaderHelper instance;
    public static RxCacheLoaderHelper getInstance(){
        if(instance == null){
            synchronized (RxCacheLoaderHelper.class){
                if(instance == null){
                    instance = new RxCacheLoaderHelper();
                }
            }
        }
        return instance;
    }

    private ICacheLoader defaultCacheLoader;

    private ILoaderPolicy defaultCachePolicy;

    private RxCacheLoaderHelper(){
        defaultCacheLoader = new DefaultCacheLoader<>();
        defaultCachePolicy = new LoadFromMemoryFirstPolicy<>();
    }

    //----------------------------------------------------------------------

    /**
     *
     * set default cache policy
     * <b>NOTE:can change at any time</b>
     * @param iLoaderPolicy
     * @return
     */
    public RxCacheLoaderHelper setLoaderPolicy(ILoaderPolicy iLoaderPolicy){
        this.defaultCachePolicy = iLoaderPolicy;
        return this;
    }

    /**
     *
     * set default cache loader
     * <b>NOTE:set before init</b>
     * @param iCacheLoader
     * @return
     */
    public RxCacheLoaderHelper setDefaultCacheLoader(ICacheLoader iCacheLoader){
        this.defaultCacheLoader = iCacheLoader;
        return this;
    }
   //------------------------------------------------------------------------

    /**
     * call it to init
     * @param context
     */
    public void init(Context context){
        defaultCacheLoader.init(context);
    }

    public void clear(Context context){
        defaultCacheLoader.clear(context);
    }

    public void clear(Context context,ICacheLoader iCacheLoader){
        iCacheLoader.clear(context);
    }


    /**
     * empty Observable
     * @param <T>
     */
    public static class NoDataObservable<T> {
        public Observable<T> create(){
            return Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call(Subscriber<? super T> subscriber) {
                    subscriber.onError(new Throwable(ERROR_NO_DATA));
                }
            });
        }
    }


    /**
     *
     * load with custom cache loader and custom policy
     * @param context
     * @param url           http url path
     * @param clazz         data class
     * @param cacheLoader   custom cache loader
     * @param loadPolicy    custom load policy
     * @param <T>
     * @return
     */
    public <T> Observable<T> load(Context context,String url, Class<T> clazz,
                                  ICacheLoader<T> cacheLoader, ILoaderPolicy<T> loadPolicy){
        return loadPolicy.load(context,url,clazz,cacheLoader);
    }

    /**
     *
     * equals to call {@link #load(Context, String, Class, ICacheLoader, #DefaultCacheLoader)}
     * @param context
     * @param url
     * @param clazz
     * @param cacheLoader
     * @param <T>
     * @return
     */
    public <T> Observable<T> load(Context context,String url, Class<T> clazz,
                                  ICacheLoader<T> cacheLoader){
        return defaultCachePolicy.load(context,url,clazz,cacheLoader);
    }


    /**
     *
     * using {@link LoadFromMemoryFirstPolicy} to load with custom {@link ICacheLoader}
     * @param context
     * @param url
     * @param clazz
     * @param cacheLoader
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromMemoryFirst(Context context,String url, Class<T> clazz, ICacheLoader<T> cacheLoader){
        return new LoadFromMemoryFirstPolicy<T>()
                .load(context,url,clazz,cacheLoader)
                .subscribeOn(Schedulers.io());
    }

    /**
     *
     * using {@link LoadFromMemoryFirstPolicy} to load with defaultCacheLoader
     *
     * @param context
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromMemoryFirst(Context context,String url, Class<T> clazz){
        return loadFromMemoryFirst(context,url,clazz,defaultCacheLoader);

    }

    /**
     * using {@link LoadFromNetworkFirstPolicy} to load with custom {@link ICacheLoader}
     * @param context
     * @param url
     * @param clazz
     * @param cacheLoader
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromNetworkFirst(Context context,String url, Class<T> clazz, ICacheLoader<T> cacheLoader){
        return new LoadFromNetworkFirstPolicy<T>()
                .load(context,url,clazz,cacheLoader)
                .subscribeOn(Schedulers.io());
    }

    /**
     * using {@link LoadFromNetworkFirstPolicy} to load with defaultCacheLoader
     * @param context
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromNetworkFirst(Context context,String url, Class<T> clazz){
        return loadFromNetworkFirst(context,url,clazz,defaultCacheLoader);
    }


    /**
     * using {@link LoadFromAllAtTheSameTimePolicy} to load with custom {@link ICacheLoader}
     * @param context
     * @param url
     * @param clazz
     * @param cacheLoader
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromAllAtTheSameTime(Context context, String url, Class<T> clazz, ICacheLoader<T> cacheLoader){
        return new LoadFromAllAtTheSameTimePolicy<T>()
                .load(context,url,clazz,cacheLoader).subscribeOn(Schedulers.io());
    }

    /**
     * using {@link LoadFromAllAtTheSameTimePolicy} to load with defaultCacheLoader
     * @param context
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> loadFromAllAtTheSameTime(Context context, String url, Class<T> clazz){
        return loadFromAllAtTheSameTime(context,url,clazz,defaultCacheLoader);
    }


}
