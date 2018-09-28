package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.animation.Constants;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;

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

    @BindView(R.id.etStudentNo) EditText etStudentNo;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;

    @BindView(R.id.inputLayoutStudentNo) TextInputLayout inputLayoutStudentNo;
    @BindView(R.id.inputLayoutEmail)TextInputLayout inputLayoutEmail;
    @BindView(R.id.inputLayoutPassword) TextInputLayout inputLayoutPassword;
    @BindView(R.id.inputLayoutConfirmPassword) TextInputLayout inputLayoutConfirmPassword;

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
    public void register(){
        String student_no = etStudentNo.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirm_password = etConfirmPassword.getText().toString();

        boolean isValid = true;

        // Validate student no
        if (student_no.isEmpty()) {
            inputLayoutStudentNo.setError("Student no required");
            isValid = false;
        }else {
            inputLayoutStudentNo.setErrorEnabled(false);
        }

        // Validate email
        if (email.isEmpty()){
            inputLayoutEmail.setError("Email required");
            isValid = false;
        }else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        // validate password
        if (password.trim().length() < 8) {
            inputLayoutPassword.setError("Minimum 8 characters required");
            isValid = false;
        }else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        // validate confirm password
        if (confirm_password != password) {
            inputLayoutConfirmPassword.setError("Password & confirm password must be match");
            isValid = false;
        }else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

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
    }

    @OnClick(R.id.btnDoCancel)
    public void cancel(){
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }
}
