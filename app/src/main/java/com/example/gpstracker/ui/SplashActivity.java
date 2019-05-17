package com.example.gpstracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gpstracker.R;
import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.pojo.LoginParams;
import com.google.gson.Gson;

import javax.security.auth.callback.Callback;

public class SplashActivity extends AppCompatActivity {

    private WebServiceMapper mWebServiceMapper;
    private SharedPrefManager mSharedPrefManager;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefManager = new SharedPrefManager(this);
        mWebServiceMapper = new WebServiceMapper(this);
        mGson = new Gson();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String login = mSharedPrefManager.getLogin();
        String password = mSharedPrefManager.getPassword();

        if (login.equals("") || password.equals("")) {
            startLoginActivity();
            return;
        }

        LoginParams loginParams = new LoginParams(login, password);
        String authRequest = mGson.toJson(loginParams);

        mWebServiceMapper.authenticate(authRequest, new AuthenticateCallback() {
            @Override
            public void onResponse() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
