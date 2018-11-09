package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.animation.Constants;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    Constants.TransitionType type;
    String toolbarTitle;

    @BindView(R.id.etStudentNo)
    EditText etStudentNo;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;

    @BindView(R.id.inputLayoutStudentNo)
    TextInputLayout inputLayoutStudentNo;
    @BindView(R.id.inputLayoutEmail)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;
    @BindView(R.id.inputLayoutConfirmPassword)
    TextInputLayout inputLayoutConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle("Stand Alone");
//        mToolbar.setSubtitle("Sub title");
//
//        //compatibility by JAVA
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            mToolbar.setElevation(10f);
//        }
//
//        mToolbar.inflateMenu(R.menu.main_menu);
//        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                String title = (String)item.getTitle();
//                Toast.makeText(RegisterActivity.this, title, Toast.LENGTH_SHORT).show();
//
//                switch (item.getItemId()){
//                    case R.id.save:
//                        // do stuff
//                        break;
//                    case R.id.mail:
//                        // do stuff
//                        break;
//                }
//                return true;
//            }
//        });
    }


    @OnClick(R.id.btnDoRegister)
    public void register() {
        String sStudent_no = etStudentNo.getText().toString();
        String sEmail = etEmail.getText().toString();
        String sPassword = etPassword.getText().toString();
        String sConfirm_password = etConfirmPassword.getText().toString();


        if (doValidation(sStudent_no, sEmail, sPassword, sConfirm_password)) {

            callHttpRegister(sStudent_no, sEmail, sPassword, sConfirm_password);

        }

    }

    public boolean doValidation(String student_no, String email, String password, String confirm_password) {

        boolean isValid = true;

        // Validate student no
        if (student_no.isEmpty()) {
            // inputLayoutStudentNo.setError("Student no required");
            etStudentNo.setError("Student no required");
            isValid = false;
        } else {
            inputLayoutStudentNo.setErrorEnabled(false);
        }

        // Validate email
        if (email.isEmpty()) {
            // inputLayoutEmail.setError("Email required");
            etEmail.setError("Email required");
            isValid = false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        // validate password
        if (password.trim().length() < 8) {
            // inputLayoutPassword.setError("Minimum 8 characters required");
            etPassword.setError("Minimum 8 characters required");
            isValid = false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        // validate confirm password
        if (!confirm_password.equals(password)) {
            //inputLayoutConfirmPassword.setError("Password & confirm password must be match");
            etConfirmPassword.setError("Password & confirm password must be match");
            isValid = false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return isValid;
    }

    public void callHttpRegister(String student_no, String email, String password, String confirm_password) {

        Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .createUser(student_no, email, password, confirm_password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    try {

                        String json = response.body().string();
                        Log.i(TAG, "onResponse: json : " + json);

                        JSONObject data = null;
                        data = new JSONObject(json);

                        String id = data.getString("id");
                        String message = data.getString("message");
                      storeUserId(id);

//                        invisibleProgressBar();
                        Intent goToVerifyIntent = new Intent(RegisterActivity.this, VerifyActivity.class);
                        startActivity(goToVerifyIntent);
                        finish();

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: data : " + id);
                        Log.i(TAG, "onResponse: data : " + message);

                    } catch (JSONException e) {
//                        invisibleProgressBar();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                    } catch (IOException e) {
//                        invisibleProgressBar();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());
                    }
                } else {

                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
//                        invisibleProgressBar();
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: json on error : " + errorMessage);

                    } catch (IOException e) {
//                        invisibleProgressBar();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                    } catch (JSONException e) {
//                        invisibleProgressBar();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void storeUserId(String id){
        SharedPrefManager.getInstance(getApplicationContext()).storeUserId(id);
    }

    @OnClick(R.id.btnDoCancel)
    public void cancel() {
        Intent backToLandingIntent = new Intent(RegisterActivity.this, MainActivity.class);
        backToLandingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToLandingIntent);
    }
}
