package org.bytedeco.javacv.android.recognize.example.retrofit2.models.models;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "http://192.168.43.170/projects/test-3/backend/public/api/";

    private static RetrofitInstance mInstance = null;
    private Retrofit retrofit;

    private RetrofitInstance(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }



    public static synchronized RetrofitInstance getmInstance(){
        if (mInstance == null){
            mInstance = new RetrofitInstance();
        }

        return mInstance;
    }

    public BackendClient getBackendClient(){
        return retrofit.create(BackendClient.class);
    }
}
