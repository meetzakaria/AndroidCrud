package com.example.androidcrud.util;


import com.example.androidcrud.service.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//     private static final String BASE_URL = "http://10.0.0.100:8081/";
//    private static final String BASE_URL = "http://192.168.0.119:8081/";

    private static final String BASE_URL = "http://192.168.53.45:8081/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}