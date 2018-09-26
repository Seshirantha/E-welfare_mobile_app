package org.bytedeco.javacv.android.recognize.example.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etStudetNo;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register);
        setContentView(R.layout.activity_register);

        etStudetNo = findViewById(R.id.etStudentNo);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnDoRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.i(TAG, "Created");


                String student_no = etStudetNo.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirm_password = etConfirmPassword.getText().toString();

                Call<ResponseBody> call = RetrofitClient
                        .getmInstance()
                        .getApi()
                        .createUser(student_no, email, password, confirm_password);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        // Log.i(TAG, String.valueOf(response.code()));
                        try {
                            //if (response.isSuccessful()) {
                            String s = response.body().string();
                            Log.i(TAG, s);
                            // }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i(TAG, t.getMessage());
                    }
                });

//                Call<ResponseBody> call = RetrofitClient
//                        .getmInstance()
//                        .getApi()
//                        .getFuck();
//
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                             String s =  response.body().string();
//                             Log.i(TAG, s);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Log.i(TAG, t.getMessage());
//                    }
//                });
            }
        });


    }
}
