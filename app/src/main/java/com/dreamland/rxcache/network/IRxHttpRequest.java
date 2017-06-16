package com.dreamland.rxcache.network;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by XMD on 2017/3/23.
 */

public interface IRxHttpRequest {

    @Streaming
    @GET
    Observable<ResponseBody> get(@Url String url);


    @Streaming
    @POST
    Observable<ResponseBody> post(@Url String url);

    @Streaming
    @POST
    Observable<ResponseBody> post(@Url String url, @Body Object object);

    @Streaming
    @DELETE
    Observable<ResponseBody> delete(@Url String url);
}
