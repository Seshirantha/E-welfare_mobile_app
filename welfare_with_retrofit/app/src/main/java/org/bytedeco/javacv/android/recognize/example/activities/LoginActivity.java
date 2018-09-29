package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;


import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.preferences.SharedPrefManager;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "LoginActivity";

    @BindView(R.id.etLogInStudentNo)
    EditText etLogInStudentNo;

    @BindView(R.id.etLogInPassword)
    EditText etLoginPassword;

    @BindView(R.id.inputLayoutLogInStudentNo)
    TextInputLayout textInputLayoutLogInStudentNo;

    @BindView(R.id.inputLayoutLogInPassword)
    TextInputLayout textInputLayoutLogInPasswrod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btnDoLogIn)
    public void login() {

        String sStudentNo = etLogInStudentNo.getText().toString();
        String sPassword = etLoginPassword.getText().toString();

        //        if (doValidation(sStudentNo, sPassword)){

           callHttpLogin("se/2013/005", "12345678");

            //  callRetrofit2();


    }

        // startActivity(new Intent(LoginActivity.this, HomeActivity.class));

//    }


    public void callHttpLogin(String studentNo, String password){

        Call<ResponseBody> loginCall = RetrofitClient
                .getmInstance()
                .getApi()
                .loginUser(studentNo, password);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                // Log.i(TAG, "onResponse: Json: " + response.toString());

                try {

                    String json = response.body().string();
                     Log.i(TAG, "onResponse: json : " + json);

                    JSONObject data = null;
                    data = new JSONObject(json);

                    // save access token in shared preference
                    String token = data.getString("token");
                    storeToken(token);
                     Log.i(TAG, "onResponse: data : " + token);

                }catch (JSONException e){

                    Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                } catch (IOException e) {
                    Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.i(TAG, t.getMessage());
            }

        });


        // how to get the access token from shredpreferences
        String savedToken = SharedPrefManager.getInstance(this).getToken();
        Log.i(TAG, "Saved Token : " + savedToken);

    }

    public void storeToken(String token ){
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    public boolean doValidation(String studentNo, String password){

        boolean isValid = true;

        // Validate student no
        if (studentNo.isEmpty()) {
            // inputLayoutStudentNo.setError("Student no required");
            etLogInStudentNo.setError("Student no required");
            isValid = false;
        }else {
            textInputLayoutLogInStudentNo.setErrorEnabled(false);

        }

        // validate password
        if (password.trim().length() < 8) {
            // inputLayoutPassword.setError("Minimum 8 characters required");
            etLoginPassword.setError("Minimum 8 characters required");
            isValid = false;
        }else {
            textInputLayoutLogInPasswrod.setErrorEnabled(false);
        }

        return isValid;
    }




    @OnClick(R.id.btnDoLogInCancel)
    public void doLoginCancel() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }


}
