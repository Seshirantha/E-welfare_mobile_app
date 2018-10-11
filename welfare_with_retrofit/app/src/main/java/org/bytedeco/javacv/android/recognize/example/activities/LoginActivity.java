package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


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

    @BindView(R.id.loginProgressBar)
    ProgressBar loginProgressBar;


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

        if (doValidation(sStudentNo, sPassword)) {

            callHttpLogin(sStudentNo, sPassword);

        }
    }

    public void callHttpLogin(String studentNo, String password) {
        visibleProgressBar();
        Call<ResponseBody> loginCall = RetrofitClient
                .getmInstance()
                .getApi()
                .loginUser(studentNo, password);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                // Log.i(TAG, "onResponse: Json: " + response.toString());
                if (response.isSuccessful()) {

                    try {

                        String json = response.body().string();
                        Log.i(TAG, "onResponse: json : " + json);

                        JSONObject data = null;
                        data = new JSONObject(json);

                        // save access token in shared preference
                        String token = data.getString("token");
                        storeToken(token);

                        String id = data.getString("id");
                        storeUserId(id);

                        String role = data.getString("role");
                        storeUserRole(role);

                        invisibleProgressBar();
                        Intent goToHomeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goToHomeIntent);
                        finish();

                        Log.i(TAG, "onResponse: data : " + token);

                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());
                    }
                } else {

                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
                        invisibleProgressBar();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: json on error : " + errorMessage);

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                invisibleProgressBar();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }

        });


//        // how to get the access token from shredpreferences
//        String savedToken = SharedPrefManager.getInstance(this).getToken();
//        Log.i(TAG, "Saved Token : " + savedToken);

    }

    // store token
    public void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    // store id
    public void storeUserId(String id) {
        SharedPrefManager.getInstance(getApplicationContext()).storeUserId(id);
    }

    // store role
    public void storeUserRole(String role) {
        SharedPrefManager.getInstance(getApplicationContext()).storeUserType(role);
    }


    private boolean doValidation(String studentNo, String password) {

        boolean isValid = true;

        // Validate student no
        if (studentNo.isEmpty()) {
            // inputLayoutStudentNo.setError("Student no required");
            etLogInStudentNo.setError("Student no required");
            isValid = false;
        } else {
            textInputLayoutLogInStudentNo.setErrorEnabled(false);

        }

        // validate password
        if (password.trim().length() < 8) {
            // inputLayoutPassword.setError("Minimum 8 characters required");
            etLoginPassword.setError("Minimum 8 characters required");
            isValid = false;
        } else {
            textInputLayoutLogInPasswrod.setErrorEnabled(false);
        }

        return isValid;
    }

    public void visibleProgressBar () {
        loginProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void invisibleProgressBar () {
        loginProgressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @OnClick(R.id.btnDoLogInCancel)
    public void doLoginCancel() {
        Intent backToLandingIntent = new Intent(LoginActivity.this, MainActivity.class);
        backToLandingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToLandingIntent);
    }


}
