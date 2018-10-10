package org.bytedeco.javacv.android.recognize.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    // register
    @Headers({"Accept: application/json"
    //  , "Content-Type : application/json"
    })
    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> createUser(
            @Field("student_no") String student_no,
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirm_password") String confirm_assword
    );

    // login
    @Headers({"Accept: application/json"
            //  , "Content-Type : application/json"
    })
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginUser(
            @Field("student_no") String student_no,
            @Field("password") String password
    );

    // logout
    @GET("logout")
    @Headers({"Accept: application/json"
        // "Content-Type : application/json"
    })
    Call<ResponseBody> logoutUser(
            @Header("Authorization") String authToken
    );

    // verification
    @Headers({"Accept: application/json"
    // , "Content-Type : application/json"
    })
    @FormUrlEncoded
    @POST("verify/{id}")
    Call<ResponseBody> verifyUser(
            @Path("id") String id,
            @Field("code") String code
    );

    // get user data for home page
    @Headers({"Accept: application/json"
    })
    @GET("user/{id}")
    Call<ResponseBody> getUserData(
            @Header("Authorization") String authToken,
            @Path("id") String id

    );

    // Admin login for register the user

    @Headers({"Accept: application/json"
            // , "Content-Type : application/json"
    })
    @FormUrlEncoded
    @POST("adminlogin")
    Call<ResponseBody> loginAdmin(
            @Header("Authorization") String authToken,
            @Field("student_no") String userName,
            @Field("email") String Password
    );


}
