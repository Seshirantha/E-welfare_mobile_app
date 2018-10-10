package org.bytedeco.javacv.android.recognize.example.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.43.170/projects/welfare_web_app/backend/public/api/";
    private static final String IMAGE_URL = "http://192.168.43.170/projects/welfare_web_app/backend/public/";

    private static final String AUTH = "";
    private static RetrofitClient mInstance = null;
    private Retrofit retrofit = null ;

    private RetrofitClient (){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", AUTH)
                                        .method(original.method(), original.body());
                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }
                ).build();

       // Gson gson = new GsonBuilder().serializeNulls().create();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(okHttpClient)
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

    public static String getImageUrl() {
        return IMAGE_URL;
    }
}
