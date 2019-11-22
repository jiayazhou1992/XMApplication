package com.xiaomawang.family.data.repository;

import android.os.Build;
import android.text.TextUtils;

import com.xiaomawang.commonlib.utils.FunctionUtils;
import com.xiaomawang.commonlib.utils.dev.app.AppUtils;
import com.xiaomawang.family.data.remote.Api;

import java.net.Proxy;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataRepository {

    private volatile static Api remoteApi;

    public static Api getApi() {
        if (remoteApi == null) {
            synchronized (DataRepository.class) {
                createRemoteService();
            }
        }
        return remoteApi;
    }

    private static void createRemoteService() {
        //TODO 初始化网络
        OkHttpClient client = getOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        remoteApi = retrofit.create(Api.class);
    }

    /**
     * OkHttpClient
     * @return
     */
    private static OkHttpClient getOkHttpClient(){

        final String channel_from = FunctionUtils.getChannelFrom();
//        final String manufacturer = SensorsDataUtils.getManufacturer();

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder newClientBuilder = client.newBuilder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        if (AppUtils.isAppDebug()) {
            newClientBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }else {
            newClientBuilder.proxy(Proxy.NO_PROXY);
        }

        OkHttpClient newClient = newClientBuilder.build();

        return newClient;
    }
}
