package com.example.gpstracker.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gpstracker.R;
import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.pojo.Coordinates;
import com.example.gpstracker.pojo.GeoData;
import com.example.gpstracker.ui.MainActivity;
import com.example.gpstracker.util.NotificationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TrackerService extends Service {
    private static final String TAG = "TrackerService";

    public static final int MSG_WHAT_START = 234;
    public static final int MSG_WHAT_STOP = 253;

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";

    public static final String ACTION_START_TRACKING = "ACTION_START_TRACKING";
    public static final String ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING";

    public static final String ARGUMENT = "ARGUMENT";
    public static final String MESSAGE_STATUS = "MSG_STATUS";
    public static final String FILTER_STATUS_BROADCAST = "com.mirea.GpsTracker.TrackerService";

    private boolean mStatus;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private LocationManager mLocationManager;

    private WebServiceMapper mWebServiceMapper;
    private SharedPrefManager mSharedPrefManager;
    private Gson mGson;

    private Executor mExecutor;
    private Messenger mMessenger;

    @SuppressLint("HandlerLeak")
    private Handler incomingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_START:
                    startTracking();
                    break;
                case MSG_WHAT_STOP:
                    stopTracking();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private final Runnable mTrackTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runn test");

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                sendStatus();
                return;
            }

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(mExecutor, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, "location");

                            Coordinates coordinates = new Coordinates(
                                    String.valueOf(location.getLongitude()),
                                    String.valueOf(location.getLatitude()));

                            GeoData geoData = new GeoData(coordinates, mSharedPrefManager.getCarNumber());
                            final String geoPost = mGson.toJson(geoData);

                            mWebServiceMapper.updateGps(geoPost);

                        }
                    });

            mHandler.postDelayed(this, 1000 * 60);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mStatus = false;
        mHandlerThread = new HandlerThread("star_tracking");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mExecutor = Executors.newSingleThreadExecutor();
        mSharedPrefManager = new SharedPrefManager(this);
        mWebServiceMapper = new WebServiceMapper(this);
        mGson = new Gson();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (Objects.requireNonNull(action)) {
                case ACTION_START_SERVICE:
                    sendStatus();
                    break;
                case ACTION_START_TRACKING:
                    startTracking();
                    break;
                case ACTION_STOP_TRACKING:
                    stopTracking();
                    break;
            }
        }
        return START_STICKY;
    }


    public void removePeriodicTask() {
        mHandler.removeCallbacks(mTrackTask);
    }

    public void startTracking() {
        if (mStatus) {
            sendStatus();
            return;
        }
        startAsForeground();
        prepareTask();
        startPeriodicTask();
        mStatus = true;
        sendStatus();
    }

    public void stopTracking() {
        stopForeground(true);
        removePeriodicTask();
        mStatus = false;
        sendStatus();
    }

    private void sendStatus() {
        String msg;
        if (mStatus)
            msg = "Сервис включен.";
        else
            msg = "Сервис выключен.";

        Intent intent = new Intent(FILTER_STATUS_BROADCAST);
        intent.putExtra(ARGUMENT, mStatus);
        intent.putExtra(MESSAGE_STATUS, msg);
        sendBroadcast(intent);
    }

    private void prepareTask() {
    }

    private void startPeriodicTask() {
        mHandler.post(mTrackTask);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(incomingHandler);
        return mMessenger.getBinder();
    }

    private void startAsForeground() {

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.ic_baseline_gps_fixed_24px);

        // Make the notification max priority.
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);


        //Убрать коммент если будет нужна кнопка в нотификации
        // Add Turn off button intent in notification.
        /*Intent turnoffIntent = new Intent(this, TrackerService.class);
        turnoffIntent.setAction(ACTION_STOP_TRACKING);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, turnoffIntent, 0);
        NotificationCompat.Action turnoffAction = new NotificationCompat.Action(R.drawable.ic_baseline_gps_off_24px, "Turn off tracker", pendingStopIntent);
        builder.addAction(turnoffAction);*/

        builder.setContentTitle("GPS tracker");
        builder.setContentText("Данные передаются на сервер.");

        int requestID = (int) System.currentTimeMillis();
        Intent contentIntent = new Intent(this, MainActivity.class);
        //contentIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(this, requestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingContentIntent);


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }
}
