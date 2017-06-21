package com.dreamland.rxcache.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.URLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by XMD on 2017/3/23.
 */

public class RxHttp {
    private static final String TAG = RxHttp.class.getSimpleName();
    private static final Pattern P = Pattern.compile("^(http[s]?://[a-zA-Z\\.0-9:\\-]{1,})[/](.+)");

    private static boolean checkURL(String url, @NonNull String[] urls) {
        if (!URLUtil.isValidUrl(url)) {
            return false;
        }
        Matcher matcher = P.matcher(url);
        if (matcher.find()) {
            urls[0] = matcher.group(1);
            urls[1] = matcher.group(2);
            return true;
        } else {
            return false;
        }
    }
    //-----------------------------------------------------------------------------------------------

    private static Observable<ResponseBody> invalidUrl = Observable.create(new Observable.OnSubscribe<ResponseBody>() {
        @Override
        public void call(Subscriber<? super ResponseBody> subscriber) {
            subscriber.onError(new Throwable("invalid url"));
        }
    });

    //------------------------------Basic GET/POST/DELETE method-------------------------------------
    public static @NonNull Observable<ResponseBody> get(@NonNull String url) {
        String[] urls = new String[2];
        if (!checkURL(url, urls)) {
            return invalidUrl;
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(urls[0]);
        Retrofit retrofit = builder.build();
        return retrofit.create(IRxHttpRequest.class).get(urls[1]);
    }

    public static Observable<ResponseBody> post(@NonNull String url, @Nullable Object body) {
        String[] urls = new String[2];
        if (!checkURL(url, urls)) {
            return invalidUrl;
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(urls[0]);
        if(body != null){
            builder.addConverterFactory(LoganSquareConverterFactory.create(body.getClass()));
        }
        Retrofit retrofit = builder.build();
        return body == null?retrofit.create(IRxHttpRequest.class).post(urls[1]):
                retrofit.create(IRxHttpRequest.class).post(urls[1],body);
    }

    public static Observable<ResponseBody> delete(@NonNull String url) {
        String[] urls = new String[2];
        if (!checkURL(url, urls)) {
            return invalidUrl;
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(urls[0]);
        Retrofit retrofit = builder.build();
        return retrofit.create(IRxHttpRequest.class).delete(urls[1]);
    }

    //----------------------------------------------------------------------------------------------------

}
