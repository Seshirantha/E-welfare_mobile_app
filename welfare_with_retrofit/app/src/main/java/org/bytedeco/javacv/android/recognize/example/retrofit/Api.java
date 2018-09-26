package org.bytedeco.javacv.android.recognize.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> createUser(
            @Field("student_no") String student_no,
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirm_password") String confirm_assword
    );


    @GET("fuck")
    Call<ResponseBody> getFuck();

}
