package com.xiaomawang.commonlib.data.remote.interceptor;


import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    protected Gson gson;

    public HeaderInterceptor() {
        this.gson = new Gson();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Request
        Request original = chain.request();

        Request.Builder request_builder = original.newBuilder();

        Map<String, String> otherHeader = getHeaders();
        if (otherHeader != null && otherHeader.size() > 0){
            Set<String> stringSet = otherHeader.keySet();
            for (String key: stringSet){
                request_builder.header(key, otherHeader.get(key));
            }
        }

        Request request = request_builder.build();

        // Response
        Response response = chain.proceed(request);
        Headers headers = response.headers();

        return response;
    }

    public Map<String, String> getHeaders(){
        return null;
    }

}
