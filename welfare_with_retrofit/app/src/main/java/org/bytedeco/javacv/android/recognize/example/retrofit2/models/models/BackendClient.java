package org.bytedeco.javacv.android.recognize.example.retrofit2.models.models;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import org.bytedeco.javacv.android.recognize.example.retrofit2.models.models.LoginReponse;

public interface BackendClient {
//    @FormUrlEncoded
    @POST("login")
    Call<LoginReponse> login(@Body LoginRequest loginRequest);

//    @FormUrlEncoded
//    @POST("login")
//    Call<LoginReponse> login(
//            @Field("student_no") String student_no,
//            @Field("password") String password
//    );

    @GET("dashborad")
    Call<ResponseBody> getDashBoard(@Header("Authorization") String authToken);

}
