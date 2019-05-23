package com.example.gpstracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.pojo.updateLocationRequest.LoginParams;
import com.example.gpstracker.ui.main.MainActivity;
import com.example.gpstracker.util.callbacks.AuthenticateCallback;
import com.google.gson.Gson;

import java.net.UnknownHostException;

public class SplashActivity extends AppCompatActivity {

    private WebServiceMapper mWebServiceMapper;
    private SharedPrefManager mSharedPrefManager;
    private Gson mGson;

    private String mLogin;
    private String mPassword;

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
        if (checkSavedLoginDetails())
            testAuthentication();
        else
            startLoginActivity();
    }

    private boolean checkSavedLoginDetails() {
        mLogin = mSharedPrefManager.getLogin();
        mPassword = mSharedPrefManager.getPassword();

        return !mLogin.equals("") && !mPassword.equals("");
    }

    private void testAuthentication() {
        LoginParams loginParams = new LoginParams(mLogin, mPassword);
        String authRequest = mGson.toJson(loginParams);

        mWebServiceMapper.authenticate(authRequest, new AuthenticateCallback() {
            @Override
            public void onResponse() {
                startMainActivity();
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof UnknownHostException)
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        startActivity(LoginActivity.newIntent(this));
    }

    private void startMainActivity() {
        startActivity(MainActivity.newIntent(this));
    }
}
