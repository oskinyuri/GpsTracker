package com.example.gpstracker.datasource;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String APP_PREFERENCES = "APP_PREF";
    public static final String APP_PREFERENCES_TOKEN = "APP_PREF_TOKEN";
    public static final String APP_PREFERENCES_CAR_NUMBER = "APP_PREF_CAR_NUMBER";

    public static final String APP_LOGIN = "APP_LOGIN";
    public static final String APP_PASSWORD = "APP_PASSWORD";

    private Context mContext;

    public SharedPrefManager(Context context) {
        mContext = context;
    }

    public void saveLogin(String login){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_LOGIN, login);
        editor.apply();
    }

    public void savePassword(String password){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PASSWORD, password);
        editor.apply();
    }

    public String getLogin(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(APP_LOGIN, "");
    }

    public String getPassword(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(APP_PASSWORD, "");
    }

    public String getToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(APP_PREFERENCES_TOKEN, "null");
    }

    public void saveToken(String token){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_TOKEN, token);
        editor.apply();
    }

    public String getCarNumber(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(APP_PREFERENCES_CAR_NUMBER, "ad1c018b-0e1c-4a87-b71f-c05c21bf622d");
    }

    public void saveCarNumber(String carNumber){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_CAR_NUMBER, carNumber);
        editor.apply();
    }


}
