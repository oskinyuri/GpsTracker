package com.example.gpstracker.ui.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
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

    private static final int MY_PERMISSIONS_REQUEST = 234;
    private static final int REQUEST_CHECK_SETTINGS = 235;

    private MainView mView;
    private Context mContext;

    private SharedPrefManager mSharedPrefManager;

    private ServiceConnection mConnection;
    private TrackerService mService;
    private boolean mBound;

    private LocationRequest mLocationRequest;

    MainPresenter(Context context) {
        mContext = context;
        mSharedPrefManager = new SharedPrefManager(mContext);

    }

    void onAttach(MainView mView) {
        this.mView = mView;
        checkPermissions();
        mView.setCarNumber(mSharedPrefManager.getCarNumber());

        Intent intent = new Intent(mContext, TrackerService.class);

        createServiceConnection();
        mContext.startService(intent);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000 * 60);
        mLocationRequest.setFastestInterval(1000 * 60);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void changeStatus(boolean status) {
        if (mView == null)
            return;

        if (status) {
            mView.setServiceStatus("Включен");
        } else {
            mView.setServiceStatus("Выключен");
        }
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

    public String getLastCarNumber() {
        return mSharedPrefManager.getCarNumber();
    }

    void onDetach() {
        mView = null;
        if (mBound) {
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

    private void updateMessage(){
        //TODO implement method

    }

    //TODO add broadcastReceiver
}

