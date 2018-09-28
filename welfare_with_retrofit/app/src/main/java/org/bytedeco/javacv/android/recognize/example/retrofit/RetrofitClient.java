package org.bytedeco.javacv.android.recognize.example.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.43.170/projects/test-3/backend/public/api/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getmInstance(){
        if (mInstance == null){
            mInstance = new RetrofitClient();
        }

        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}