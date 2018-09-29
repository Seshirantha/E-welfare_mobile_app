package org.bytedeco.javacv.android.recognize.example.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "welfaresharedperf";
    private static final String KEY_ACCESS_TOKEN = "token";

    private static Context mContext;
    private static SharedPrefManager mSharedPrefManager;

    private SharedPrefManager (Context context){
        mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if (mSharedPrefManager == null) {
            mSharedPrefManager = new SharedPrefManager(context);
        }

        return mSharedPrefManager;
    }

    public boolean storeToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);

    }



}
