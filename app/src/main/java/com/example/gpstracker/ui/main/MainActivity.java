package com.example.gpstracker.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gpstracker.R;
import com.example.gpstracker.ui.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainView {
    private EditText mCarNumber;
    private TextView mStatus;
    private Button mStartBtn;
    private Button mStopBtn;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mPresenter = new MainPresenter(this);
    }

    private void initViews() {
        mCarNumber = findViewById(R.id.carNumberEditText);
        mStartBtn = findViewById(R.id.startBtn);
        mStopBtn = findViewById(R.id.stopBtn);
        mStatus = findViewById(R.id.serviceStatusTV);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onAttach(this);
        initListeners();
    }

    @Override
    protected void onPause() {
        mPresenter.onDetach();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initListeners() {
        mStartBtn.setOnClickListener(v -> mPresenter.onStartButtonClicked(mCarNumber.getText().toString()));
        mStopBtn.setOnClickListener(v-> mPresenter.onStopButtonClicked());
    }

    @Override
    public void setCarNumber(String carNumber) {
        mCarNumber.setText(carNumber);
    }

    @Override
    public void setServiceStatus(String status) {
        mStatus.setText(status);
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPresenter.onPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_btn:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        mPresenter.onLogoutButtonClicked();
        startActivity(LoginActivity.newIntent(this));
    }



    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
