package com.example.gpstracker.ui.main;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpstracker.R;
import com.example.gpstracker.ui.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements MainView {

    private ConstraintLayout mRootConstraintLayout;

    private EditText mCarNumber;
    private TextView mStatusTextView;
    private TextView mMessageTextView;
    private Button mStartButton;
    private Button mStopButton;
    private Button mAlarmButton;
    private ImageView mTrackingStatusImageView;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();

        mPresenter = new MainPresenter(this);
    }

    private void initViews() {
        mRootConstraintLayout = findViewById(R.id.main_root_constraint_layout);
        mCarNumber = findViewById(R.id.main_service_car_number_text_input_edit_text);
        mStartButton = findViewById(R.id.main_service_start_button);
        mStopButton = findViewById(R.id.main_service_stop_button);
        mStatusTextView = findViewById(R.id.main_service_tracking_status_text_view);
        mMessageTextView = findViewById(R.id.main_message_text_text_view);
        mAlarmButton = findViewById(R.id.mainAlarmButton);
        mTrackingStatusImageView = findViewById(R.id.main_service_tracking_status_image_view);
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
        mStartButton.setOnClickListener(v -> mPresenter.onStartButtonClicked(mCarNumber.getText().toString()));
        mStopButton.setOnClickListener(v -> mPresenter.onStopButtonClicked());
        mAlarmButton.setOnClickListener(v -> mPresenter.onAlarmButtonClicked());
    }

    private void animateToAlert() {
        int colorFrom = ((ColorDrawable) mRootConstraintLayout.getBackground()).getColor();
        int colorTo = getResources().getColor(R.color.colorAlert);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(animator -> mRootConstraintLayout.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    private void animateFromAlert() {
        int colorFrom = ((ColorDrawable) mRootConstraintLayout.getBackground()).getColor();
        int colorTo = getResources().getColor(R.color.colorPrimary);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(animator -> mRootConstraintLayout.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    @Override
    public void setCarNumber(String carNumber) {
        mCarNumber.setText(carNumber);
    }

    @Override
    public void setServiceStatus(String status) {
        mStatusTextView.setText(status);
    }

    @Override
    public void setMessage(String message) {
        mMessageTextView.setText(message);
    }

    @Override
    public void toAlert() {
        animateToAlert();
        mAlarmButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void fromAlert() {
        animateFromAlert();
        mAlarmButton.setBackgroundColor(getResources().getColor(R.color.colorAlert));
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void setAlarmButtonEnabled(boolean enabled) {
        mAlarmButton.setEnabled(enabled);
    }

    @Override
    public void setStartButtonEnabled(boolean enabled) {
        mStartButton.setEnabled(enabled);
    }

    @Override
    public void setStopButtonEnabled(boolean enabled) {
        mStopButton.setEnabled(enabled);
    }

    @Override
    public void setImageTrackingIsOff() {
        mTrackingStatusImageView.setImageDrawable(getDrawable(R.drawable.ic_tracking_off));
    }

    @Override
    public void setImageTrackingIsOn() {
        mTrackingStatusImageView.setImageDrawable(getDrawable(R.drawable.ic_tracking_on));
    }

    @Override
    public void setCarNumberEnabled(boolean enabled) {
        mCarNumber.setEnabled(enabled);
    }

    @Override
    public void setAlertButtonVisible(boolean visible) {
        mAlarmButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPresenter.onPermissionsResult(requestCode, permissions, grantResults);
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

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
