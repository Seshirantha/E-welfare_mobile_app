package org.bytedeco.javacv.android.recognize.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.preferences.SharedPrefManager;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.homeProgressBar)
    ProgressBar homeProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getData() {

    }



    public void logout() {
//        visibleProgressBar();
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
}
