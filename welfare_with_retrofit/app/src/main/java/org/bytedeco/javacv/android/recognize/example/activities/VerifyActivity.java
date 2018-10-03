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

public class VerifyActivity extends AppCompatActivity {

    private static final String TAG = "VerifyActivity";

    @BindView(R.id.verifyProgressBar)
    ProgressBar verifyProgressBar;

    @BindView(R.id.etVerifyCode)
    EditText etVerifyCode;

    @BindView(R.id.inputLayoutVerifyCode)
    TextInputLayout inputLayoutVerifyCode;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnDoVerify)
    public void doVerification() {
        String code = etVerifyCode.getText().toString();
        if (doValidations(code)) {
            callHttpVerification(code);
        }

    }

    public void callHttpVerification (String code){

        String userId = SharedPrefManager.getInstance(this).getUserId();

        Call<ResponseBody> verifyCall = RetrofitClient
                .getmInstance()
                .getApi()
                .verifyUser(userId ,code);

        verifyCall.enqueue(new Callback<ResponseBody>() {
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

//                        String id = data.getString("id");
//                        storeUserId(id);

                        String role = data.getString("role");
                        storeUserRole(role);

                        invisibleProgressBar();
                        Intent goToHomeIntent = new Intent(VerifyActivity.this, HomeActivity.class);
                        goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goToHomeIntent);
                        finish();

                        Log.i(TAG, "onResponse: data : " + data);


                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(VerifyActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: json on error : " + errorMessage);

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                invisibleProgressBar();
                Toast.makeText(VerifyActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });

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

    // visible progressbar
    public void visibleProgressBar () {
        verifyProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // visible progressbar
    public void invisibleProgressBar () {
        verifyProgressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // do validations
    public boolean doValidations(String code) {

        boolean isValid = true;

        // Validate student no
        if (code.isEmpty()) {
            // inputLayoutStudentNo.setError("Student no required");
            etVerifyCode.setError("Code is required");
            isValid = false;
        } else {
            inputLayoutVerifyCode.setErrorEnabled(false);
        }

        return isValid;
    }

    @OnClick(R.id.btnDoVerifyCancel)
    public void doVerifyCancel() {

        Intent goToHomeIntent = new Intent(VerifyActivity.this, MainActivity.class);
        goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHomeIntent);

    }
}
