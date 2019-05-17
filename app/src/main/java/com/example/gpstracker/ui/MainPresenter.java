package com.example.gpstracker.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.services.TrackerService;
import com.google.gson.Gson;

public class MainPresenter {

    private MainView mView;
    private Context mContext;

    private SharedPrefManager mSharedPrefManager;
    private LocationManager mLocationManager;
    private WebServiceMapper mWebServiceMapper;
    private Gson mGson;

    private Messenger mService = null;
    private boolean bound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            bound = false;
        }
    };

    public MainPresenter(Context context) {
        mContext = context;
        mWebServiceMapper = new WebServiceMapper(mContext);
        mSharedPrefManager = new SharedPrefManager(mContext);
        mGson = new Gson();

    }

    public void onAttach(MainView mView) {
        this.mView = mView;
        registerBroadcast();
        mView.setCarNumber(mSharedPrefManager.getCarNumber());

        Intent intent = new Intent(mContext, TrackerService.class);
        intent.setAction(TrackerService.ACTION_START_SERVICE);
        mContext.startService(intent);

        /*mContext.bindService(new Intent(mContext, TrackerService.class), mConnection,
                Context.BIND_AUTO_CREATE);*/


    }

    private void registerBroadcast() {
        BroadcastReceiver statusBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean status = intent.getBooleanExtra(TrackerService.ARGUMENT, false);
                changeStatus(status);
                Toast.makeText(mContext, intent.getStringExtra(TrackerService.MESSAGE_STATUS), Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TrackerService.FILTER_STATUS_BROADCAST);
        mContext.registerReceiver(statusBroadcast, intentFilter);
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

    public void onDetach() {
        mView = null;
        /*if (bound) {
            mContext.unbindService(mConnection);
            bound = false;
        }*/
    }

    public String getDefaultCarNumber() {
        return mSharedPrefManager.getCarNumber();
    }

    public void startService(String carNumber) {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mView.requestPermissions();

            Toast.makeText(mContext, "Not permissions!", Toast.LENGTH_SHORT).show();
            return;
        }

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            Toast.makeText(mContext, "Не включен GPS!", Toast.LENGTH_SHORT).show();
            return;
        }

        mSharedPrefManager.saveCarNumber(carNumber);

        /*if (!bound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, TrackerService.MSG_WHAT_START, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        Intent intent = new Intent(mContext, TrackerService.class);
        intent.setAction(TrackerService.ACTION_START_TRACKING);
        mContext.startService(intent);

    }

    public void stopService() {

        Message msg = Message.obtain(null, TrackerService.MSG_WHAT_STOP, 0, 0);
        /*try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

        Intent intent = new Intent(mContext, TrackerService.class);
        intent.setAction(TrackerService.ACTION_STOP_TRACKING);
        mContext.startService(intent);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("GPS выключен. Включить?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) ->
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) ->
                        dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void logout() {
        mSharedPrefManager.savePassword("");
        mSharedPrefManager.saveLogin("");
    }
}
