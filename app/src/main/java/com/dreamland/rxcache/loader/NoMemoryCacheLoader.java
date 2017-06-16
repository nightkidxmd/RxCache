package com.dreamland.rxcache.loader;


import rx.Observable;

/**
 * Created by XMD on 2017/6/15.
 * Only have disk and network
 */
public class NoMemoryCacheLoader extends DefaultCacheLoader {
    public NoMemoryCacheLoader(int maxSize) {

    }

    public NoMemoryCacheLoader() {

    }

    @Override
    public Observable loadFromMemory(Observable observable) {
        return Observable.empty();
    }
}
