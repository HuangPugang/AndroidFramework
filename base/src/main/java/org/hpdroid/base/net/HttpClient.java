package org.hpdroid.base.net;

import com.hpdroid.base.R;
import org.hpdroid.base.constant.API;
import org.hpdroid.base.net.convert.GsonConverterFactory;
import org.hpdroid.base.net.interceptor.LogInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * http请求客户端
 * Created by paul on 16/10/11.
 */

public class HttpClient {
    protected static Retrofit retrofit = null;

    private HttpClient() {
    }

    private static Retrofit getRetrofit() {

        if (retrofit == null) {
            synchronized (HttpClient.class) {
                int[] certificates = {R.raw.text};
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new LogInterceptor())
//                        .socketFactory(HttpsFactory.getSSLSocketFactory(CtxHelper.getApp(), certificates))
                        .build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(API.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(okHttpClient)
                        .build();
            }
        }

        return retrofit;
    }


    public static <T> T getAPIService(Class<T> clazz) {
        return getRetrofit().create(clazz);
    }


}
