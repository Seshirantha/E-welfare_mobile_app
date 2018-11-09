package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.preferences.SharedPrefManager;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private String globalStudentName = "";

    @BindView(R.id.homeProgressBar)
    ProgressBar homeProgressBar;

    @BindView(R.id.tvHomeSchol)
    TextView tvHomeScholarship;

    @BindView(R.id.tvHomeDuration)
    TextView tvHomeDuration;

    @BindView(R.id.tvHomeScholStatus)
    TextView tvHomeScholStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent goToOpevCvRecognizeActivity = new Intent(HomeActivity.this, OpenCvRecognizeActivity.class);
                goToOpevCvRecognizeActivity.putExtra("studentName", globalStudentName);
                startActivity(goToOpevCvRecognizeActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getDashboardData();
        getUserData(navigationView);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_face_register) {
            String userType = SharedPrefManager.getInstance(this).getUserType();
            if (!userType.equals("ROLE_SUPERADMINA")){
                Log.i(TAG, userType);
                Toast.makeText(HomeActivity.this, userType, Toast.LENGTH_LONG).show();
                doAdminLogin();
            }else {
                Toast.makeText(HomeActivity.this, "You are Super Admin", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_face_recognize) {

            Intent goToOpenCvRecognze = new Intent(HomeActivity.this, OpenCvRecognizeActivity.class);
            goToOpenCvRecognze.putExtra("studentName", globalStudentName);
            startActivity(goToOpenCvRecognze);

        }
//        else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // get user data from backend
    // also,
    public void getDashboardData() {


        // visibleProgressBar();
        String userToken = "Bearer " + SharedPrefManager.getInstance(this).getToken();
        String userId = SharedPrefManager.getInstance(this).getUserId();

        Call<ResponseBody> callToUserData = RetrofitClient
                .getmInstance()
                .getApi()
                .dashboardData(userToken, userId);

        callToUserData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {


                        String json = response.body().string();
                         Log.i(TAG, "onResponse :On Dashboard json : " + json);


                        JSONArray jsonArray = null;
                        jsonArray = new JSONArray(json);
                        Log.i(TAG, "onResponse dashboard: data : " + jsonArray.get(0).toString());

                        JSONObject data = null;
                        data = new JSONObject(jsonArray.get(0).toString());
                        Log.i(TAG, "onResponse dashboard: schol : " + data.getString("scholership_name"));

                        // studentName.setText(data.getString("first_name") + " " + data.getString("last_name"));
                        //studentEmail.setText(data.getString("email"));
                        tvHomeScholarship.setText(data.getString("scholership_name"));
                        tvHomeDuration.setText(data.getString("period"));
                        tvHomeScholStatus.setText(data.getString("signature_status"));

                        // invisibleProgressBar();


                    } catch (JSONException e) {
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse:On Dashboard jsonException: " + e.getMessage());

                    } catch (IOException e) {
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse:On Dashboard jsonException: " + e.getMessage());
                    }
                }else {
                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse:On Dashboard json on error : " + errorMessage);

                    } catch (IOException e) {
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException On Dashboard : " + e.getMessage());
                    } catch (JSONException e) {
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException On Dashboard: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // invisibleProgressBar();
                // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });

    }

    // get user data from backend
    // also,
    public void getUserData(NavigationView navigationView) {
        View navViewHeader= navigationView.getHeaderView(0);
        TextView studentName = (TextView)navViewHeader.findViewById(R.id.tvStudentName);
        TextView studentEmail = (TextView)navViewHeader.findViewById(R.id.tvStudentEmail);
        ImageView studentImage = (ImageView)navViewHeader.findViewById(R.id.ivUserImage);


        // get image  base url
        String baseUrl = RetrofitClient.getImageUrl();

        // visibleProgressBar();
        String userToken = "Bearer " + SharedPrefManager.getInstance(this).getToken();
        String userId = SharedPrefManager.getInstance(this).getUserId();

        Call<ResponseBody> callToUserData = RetrofitClient
                .getmInstance()
                .getApi()
                .getUserData(userToken, userId);

        callToUserData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {


                        String json = response.body().string();
                        Log.i(TAG, "onResponse: json : " + json);

                        JSONObject data = null;
                        data = new JSONObject(json);
                        // Log.i(TAG, "onResponse logout: data : " + data);

//                        String name = data.getString("first_name") + " " + data.getString("last_name");
                        String url = baseUrl + data.getString("saved_path");
                        Log.i(TAG, url);
                        URL newurl = new URL(url);

//                        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//                        studentImage.setImageBitmap(mIcon_val);

                        studentName.setText(data.getString("first_name") + " " + data.getString("last_name"));
                        studentEmail.setText(data.getString("email"));

                        // set student name to global variable because, it used in
                        // OpenCvRecognizeActivity to display string, ex:  "Shirantha I know you"
                        globalStudentName = data.getString("first_name");
                        // Log.i(TAG, globalStudentName);

                        // invisibleProgressBar();


                    } catch (JSONException e) {
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                    } catch (IOException e) {
                       // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());
                    }
                }else {
                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
                        // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: json on error : " + errorMessage);

                    } catch (IOException e) {
                       // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                    } catch (JSONException e) {
                       // invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               // invisibleProgressBar();
                // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });

    }

    public void logout() {
        //visibleProgressBar();
        String userToken = "Bearer " + SharedPrefManager.getInstance(this).getToken();

        // Toast.makeText(HomeActivity.this, userToken , Toast.LENGTH_LONG).show();
        Call<ResponseBody> logoutCall = RetrofitClient
                .getmInstance()
                .getApi()
                .logoutUser(userToken);

        logoutCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    try {

                        String json = response.body().string();
//                        Log.i(TAG, "onResponse: json : " + json);

                        JSONObject data = null;
                        data = new JSONObject(json);
                     //   Log.i(TAG, "onResponse logout: data : " + data);
                        String message = data.getString("message");
//                      invisibleProgressBar();
                        Intent goToHomeIntent = new Intent(HomeActivity.this, MainActivity.class);
                        goToHomeIntent.putExtra("logoutMessage", message);
                        goToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                        startActivity(goToHomeIntent);


                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse: jsonException: " + e.getMessage());
                    }
                }else {
                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
                        invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse: json on error : " + errorMessage);

                    } catch (IOException e) {
                        invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                    } catch (JSONException e) {
                        invisibleProgressBar();
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                invisibleProgressBar();
               // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });

    }

    public void visibleProgressBar () {
        homeProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void invisibleProgressBar () {
        homeProgressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // Open dialog box for admin login
    private void doAdminLogin () {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        View adminLoginLayoutView = getLayoutInflater().inflate(R.layout.admin_login_dialog, null);
        final EditText etAdminLoginUserName = (EditText) adminLoginLayoutView.findViewById(R.id.etAdminLogInUserName);
        final EditText etAdminLoginPassword = ( EditText) adminLoginLayoutView.findViewById(R.id.etAdminLogInPassword);
        final Button btnAdminLogin = (Button) adminLoginLayoutView.findViewById(R.id.btnAdminDoLogIn);
        final Button btnAdminLoginCancel  = (Button) adminLoginLayoutView.findViewById(R.id.btnDoAdminLogInCancel);

        // do admin login
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doValidation(etAdminLoginUserName, etAdminLoginPassword)) {
                    // write api call
                    //visibleProgressBar();
                    String userToken = "Bearer " + SharedPrefManager.getInstance(HomeActivity.this).getToken();
                    String userName = etAdminLoginUserName.getText().toString();
                    String password = etAdminLoginPassword.getText().toString();

                     //Toast.makeText(HomeActivity.this, etAdminLoginUserName.getText().toString(), Toast.LENGTH_LONG).show();

                    Call<ResponseBody> adminLoginCall = RetrofitClient
                            .getmInstance()
                            .getApi()
                            .loginAdmin(
                                   userToken, userName, password
                                    );

                    adminLoginCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {

                                try {

                                    String json = response.body().string();
                                    //Log.i(TAG, "onResponse: json : " + json);

                                    JSONObject data = null;
                                    data = new JSONObject(json);
                                    Log.i(TAG, "onResponse logout: data : " + data);
                                    Intent goToOpenCVIntent = new Intent(HomeActivity.this, OpenCvRecognizeActivity.class);
                                    goToOpenCVIntent.putExtra("KEY", 1);
                                    startActivity(goToOpenCVIntent);

                                } catch (JSONException e) {
                                    invisibleProgressBar();
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "onResponse: jsonException: " + e.getMessage());

                                } catch (IOException e) {
                                    invisibleProgressBar();
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "onResponse: jsonException: " + e.getMessage());
                                }
                            }else {
                                String json = null;
                                try {
                                    json = response.errorBody().string();
                                    JSONObject data = null;
                                    data = new JSONObject(json);
                                    String errorMessage = data.getString("fail");
                                    invisibleProgressBar();
                                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    Log.i(TAG, "onResponse: json on errors : " + errorMessage);

                                } catch (IOException e) {
                                    invisibleProgressBar();
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.i(TAG, "onResponse else IOException : " + e.getMessage());
                                } catch (JSONException e) {
                                    invisibleProgressBar();
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.i(TAG, "onResponse JSONException : " + e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            invisibleProgressBar();
                            // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "onFailure : " + t.getMessage());

                        }
                    });

                }
            }
        });

        alertDialogBuilder.setView(adminLoginLayoutView);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        // cancel dialog
        btnAdminLoginCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private boolean doValidation(EditText etUsername, EditText etPassword) {
        boolean isValid = true;
        // Validate student no
        if (etUsername.getText().toString().isEmpty()) {
            // inputLayoutStudentNo.setError("Student no required");
            etUsername.setError("User name is required");
            isValid = false;
        }
        // validate password
        if (etPassword.getText().toString().trim().length() < 8) {
            // inputLayoutPassword.setError("Minimum 8 characters required");
            etPassword.setError("Minimum 8 characters required");
            isValid = false;
        }
        return isValid;
    }

}
