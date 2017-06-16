package com.dreamland.rxcache.network;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by XMD on 2017/3/23.
 */

public class LoganSquareRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    @Override
    public RequestBody convert(T value) throws IOException {
        String body = LoganSquare.serialize(value);
        return RequestBody.create(MEDIA_TYPE, body.getBytes("UTF-8"));
    }
}
