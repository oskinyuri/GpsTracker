package com.example.gpstracker.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.services.TrackerService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MainPresenter {

    public static final String ACTION_UPDATE_MESSAGE = "ACTION_UPDATE_MESSAGE ";
    public static final String EXTRA_NEW_MESSAGE = "EXTRA_NEW_MESSAGE";

    private static final int MY_PERMISSIONS_REQUEST = 234;
    private static final int REQUEST_CHECK_SETTINGS = 235;

    private MainView mView;
    private Context mContext;

    private SharedPrefManager mSharedPrefManager;

    private ServiceConnection mConnection;
    private TrackerService mService;
    private boolean mBound;

    private LocationRequest mLocationRequest;

    private BroadcastReceiver mMessageReceiver;
    private IntentFilter mMessageIntentFilter;

    MainPresenter(Context context) {
        mContext = context;
        mSharedPrefManager = new SharedPrefManager(mContext);
        createMessageReceiver();
    }

    void onAttach(MainView mView) {
        this.mView = mView;
        if (mView == null)
            return;

        checkPermissions();
        mView.setCarNumber(mSharedPrefManager.getCarNumber());

        Intent intent = new Intent(mContext, TrackerService.class);

        createServiceConnection();
        mContext.startService(intent);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        registerMessageReceiver();
    }

    private void createServiceConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                TrackerService.LocalBinder binder = (TrackerService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                changeStatus(mService.getTrackingStatus());
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
    }

    void onStartButtonClicked(String carNumber) {
        mSharedPrefManager.saveCarNumber(carNumber);
        if (mBound && !mService.getTrackingStatus()) {
            startLocationTracking();
            changeStatus(mService.getTrackingStatus());
        }
    }

    void onStopButtonClicked() {
        if (mBound) {
            mService.stopTracking();
            changeStatus(mService.getTrackingStatus());
        }
    }

    void onLogoutButtonClicked() {
        mSharedPrefManager.savePassword("");
        mSharedPrefManager.saveLogin("");
        onStopButtonClicked();
    }

    void onAlarmButtonClicked() {
        if (mBound) {
            boolean isAlarm = mService.changeAlarm();
            updateAlarmButtonUI(isAlarm);
        }
    }

    private void updateAlarmButtonUI(boolean isAlarm) {
        if (isAlarm) {
            mView.toAlert();
            vibrateAndSoundOnClick();
        } else {
            mView.fromAlert();
        }
    }

    private void vibrateAndSoundOnClick() {
        try {
            Vibrator vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
            vibe.vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000 * 20);
        mLocationRequest.setFastestInterval(1000 * 20);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void changeStatus(boolean status) {
        if (mView == null)
            return;

        if (status) {
            mView.setServiceStatus("Выполняется отслеживание");
            mView.setImageTrackingIsOn();
        } else {
            mView.setServiceStatus("Отслеживание выключено");
            mView.setImageTrackingIsOff();
            mView.fromAlert();
        }
        mView.setCarNumberEnabled(!status);
        mView.setStartButtonEnabled(!status);
        mView.setStopButtonEnabled(status);
        mView.setAlarmButtonEnabled(status);
        mView.setAlertButtonVisible(status);
    }

    private void startLocationTracking() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        createLocationRequest();
        builder.addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(mView.getViewActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(mView.getViewActivity(), locationSettingsResponse -> {
            mService.startTracking(mLocationRequest);
            changeStatus(mService.getTrackingStatus());
        });

        task.addOnFailureListener(mView.getViewActivity(), e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(mView.getViewActivity(),
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(mView.getViewActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST);
    }

    void onPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Permissions allow", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Permissions not allow", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateMessage(String message) {
        if (mView == null)
            return;
        mView.setMessage(message);
    }

    private void createMessageReceiver() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateMessage(intent.getStringExtra(EXTRA_NEW_MESSAGE));
            }
        };

        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction(ACTION_UPDATE_MESSAGE);
    }

    private void registerMessageReceiver() {
        mContext.registerReceiver(mMessageReceiver, mMessageIntentFilter);
    }

    private void unregisterMessageReceiver() {
        mContext.unregisterReceiver(mMessageReceiver);
    }

    void onDetach() {
        mView = null;
        unregisterMessageReceiver();
        if (mBound) {
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }
}

