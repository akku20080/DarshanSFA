package com.darshansfa.API;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import com.darshansfa.Utility.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nikhil on 18-05-2016.
 */
public class RetrofitAPI {

    private static RetrofitAPI retrofitAPI;
    private static Retrofit retrofit;

    private RetrofitAPI() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Interceptor.Chain chain) throws IOException {
//                Request original = chain.request();
//
//                // Request customization: add request headers
//                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Authorization", "auth-value");
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClient.addInterceptor(interceptor);

        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(interceptor);

        final OkHttpClient okHttpClient = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.Host)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public static RetrofitAPIInterface getApi() {
        if (retrofitAPI == null) {
            getInstance();
        }
        return retrofit.create(RetrofitAPIInterface.class);
    }

    public static RetrofitAPI getInstance() {
        if (retrofitAPI == null) {
            if (retrofitAPI == null) {
                retrofitAPI = new RetrofitAPI();
            }
        }
        return retrofitAPI;
    }
}
