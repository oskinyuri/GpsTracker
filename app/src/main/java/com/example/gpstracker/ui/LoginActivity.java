package com.example.gpstracker.ui;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gpstracker.R;
import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.pojo.LoginParams;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private WebServiceMapper mWebServiceMapper;
    private SharedPrefManager mSharedPrefManager;
    private Gson mGson;

    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;

    private String mLogin;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPrefManager = new SharedPrefManager(this);
        mWebServiceMapper = new WebServiceMapper(this);
        mGson = new Gson();

        initViews();
    }

    private void initViews() {
        mLoginEditText = findViewById(R.id.loginEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mSignInButton = findViewById(R.id.loginButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListeners();
    }

    private void initListeners() {
        mSignInButton.setOnClickListener(v -> {
            if (!checkAuthenticationFields())
                return;
            startAuthRequest();
        });
    }

    private boolean checkAuthenticationFields() {
        mLogin = mLoginEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();

        if (mLogin.equals("") || mPassword.equals("")) {
            failAuthenticate();
            return false;
        } else {
            return true;
        }
    }

    private void failAuthenticate() {
        Toast.makeText(getBaseContext(), "Authenticate error!", Toast.LENGTH_SHORT).show();
        mLoginEditText.setError("Неверный логин или пароль!");
        mPasswordEditText.setError("Неверный логин или пароль!");
    }

    private void startAuthRequest() {
        LoginParams loginParams = new LoginParams(mLogin, mPassword);
        String authRequest = mGson.toJson(loginParams);

        mWebServiceMapper.authenticate(authRequest, new AuthenticateCallback() {
            @Override
            public void onResponse() {
                saveLoginDetails();
                startMainActivity();
            }

            @Override
            public void onFailure(Throwable throwable) {
                failAuthenticate();
            }
        });
    }

    private void saveLoginDetails() {
        mSharedPrefManager.saveLogin(mLogin);
        mSharedPrefManager.savePassword(mPassword);
    }

    private void startMainActivity(){
        startActivity(MainActivity.newIntent(this));
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}
