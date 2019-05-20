package com.example.gpstracker.ui;

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

    private EditText mLogin;
    private EditText mPassword;
    private Button mSignInButton;

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
        mLogin = findViewById(R.id.loginEditText);
        mPassword = findViewById(R.id.passwordEditText);
        mSignInButton = findViewById(R.id.loginButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListeners();
    }

    private void initListeners() {
        mSignInButton.setOnClickListener(v -> {

            String login = mLogin.getText().toString();
            String password = mPassword.getText().toString();

            if (login.equals("") || password.equals("")) {
                failAuthenticate();
                return;
            }

            LoginParams loginParams = new LoginParams(login,password);
            String authRequest = mGson.toJson(loginParams);

            mWebServiceMapper.authenticate(authRequest, new AuthenticateCallback() {
                @Override
                public void onResponse() {
                    mSharedPrefManager.saveLogin(login);
                    mSharedPrefManager.savePassword(password);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure() {
                    failAuthenticate();
                }
            });
        });
    }

    private void failAuthenticate(){
        Toast.makeText(getBaseContext(), "Authenticate error!", Toast.LENGTH_SHORT).show();
        mLogin.setError("Неверный логин или пароль!");
        mPassword.setError("Неверный логин или пароль!");
    }

}
