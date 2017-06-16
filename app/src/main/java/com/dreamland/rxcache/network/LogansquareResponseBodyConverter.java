package com.dreamland.rxcache.network;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by XMD on 2017/3/23.
 */

public class LogansquareResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private Class<T> clazz;

    public LogansquareResponseBodyConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return  LoganSquare.parse(value.byteStream(),clazz);
    }
}
