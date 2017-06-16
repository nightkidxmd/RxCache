package com.dreamland.rxcache.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by XMD on 2017/3/23.
 */

public class LoganSquareConverterFactory extends Converter.Factory {
    private Class<?> clazz;

    public static LoganSquareConverterFactory create(Class<?> clazz){
        return new LoganSquareConverterFactory(clazz);
    }

    public LoganSquareConverterFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new LogansquareResponseBodyConverter<>(clazz);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new LoganSquareRequestBodyConverter<>();
    }
}
