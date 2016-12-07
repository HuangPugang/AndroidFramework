package org.hpdroid.base.net.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by paul on 16/10/21.
 */

public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();
        Log.e("LogInterceptor",url);
        Request.Builder requestBuilder = originalRequest.newBuilder();
        requestBuilder.url(url)
                .method(originalRequest.method(), originalRequest.body());


        Response response = chain.proceed(originalRequest);
        String result = response.body().string();
        Log.e("LogInterceptor",result);
        ResponseBody body = ResponseBody.create(response.body().contentType(), result);
        return response.newBuilder().body(body).build();
    }
}
