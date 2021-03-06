package com.example.gpstracker.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.example.gpstracker.R;
import com.example.gpstracker.datasource.SharedPrefManager;
import com.example.gpstracker.datasource.WebServiceMapper;
import com.example.gpstracker.pojo.updateLocationRequest.Fields;
import com.example.gpstracker.pojo.updateLocationRequest.GeoData;
import com.example.gpstracker.pojo.massegeRequest.MessageRequest;
import com.example.gpstracker.pojo.massegeRequest.Operand;
import com.example.gpstracker.pojo.massegeRequest.Operands;
import com.example.gpstracker.pojo.massegeRequest.Predicate;
import com.example.gpstracker.ui.main.MainPresenter;
import com.example.gpstracker.util.callbacks.UpdateLocationCallback;
import com.example.gpstracker.ui.main.MainActivity;
import com.example.gpstracker.util.NotificationUtils;
import com.example.gpstracker.util.callbacks.UpdateMessageCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class TrackerService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private LocationRequest mLocationRequest;

    private boolean isTracking = false;
    private boolean isAlarm = false;

    private Handler mHandler;

    private WebServiceMapper mWebServiceMapper;
    private SharedPrefManager mSharedPrefManager;
    private Gson mGson;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private Runnable mLocationUpdateTask;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("star_tracking");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        mSharedPrefManager = new SharedPrefManager(this);
        mWebServiceMapper = new WebServiceMapper(this);
        mGson = new Gson();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startTracking(LocationRequest locationRequest) {
        if (isTracking)
            return;

        mLocationRequest = locationRequest;
        isTracking = true;

        startAsForeground();
        createLocationUpdateTask();
        mHandler.post(mLocationUpdateTask);
    }

    public void stopTracking() {
        if (!isTracking)
            return;

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mHandler.removeCallbacks(mLocationUpdateTask);
        isTracking = false;
        stopForeground(true);
    }

    public boolean changeAlarmStatus(){
        if (!isTracking)
            return false;

        isAlarm = (!isAlarm);
        return isAlarm;
    }

    public boolean getAlarmStatus() {
        return isAlarm;
    }

    private void createLocationUpdateTask() {
        mLocationUpdateTask = () -> {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            createLocationCallback();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        };
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    String alarm = getAlarmAsString();
                    Fields fields = new Fields(
                            location.getLongitude(),
                            location.getLatitude(),
                            alarm);

                    GeoData geoData = new GeoData(fields, mSharedPrefManager.getCarNumber());
                    final String geoPost = mGson.toJson(geoData);

                    mWebServiceMapper.updateGps(geoPost, new UpdateLocationCallback() {
                        @Override
                        public void onResponse() {
                            requestNewMessage();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });
                }
            }
        };
    }

    private String getAlarmAsString(){
        if (isAlarm)
            return "t";
        else return "";
    }

    /**
     * Метод создания запроса для получения нового сообщения.
     */
    private void requestNewMessage() {
        String carNumber = mSharedPrefManager.getCarNumber();
        Operand operand = new Operand(carNumber);
        Operands operands = new Operands(operand);
        List<Operands> operandsList = new ArrayList<>();
        operandsList.add(operands);
        Predicate predicate = new Predicate(operandsList);
        MessageRequest messageRequest = new MessageRequest(predicate);
        String msgRequest = mGson.toJson(messageRequest);
        mWebServiceMapper.updateMessage(msgRequest, new UpdateMessageCallback() {
            @Override
            public void onResponse(String message) {
                Intent intent = new Intent();
                intent.setAction(MainPresenter.ACTION_UPDATE_MESSAGE);
                intent.putExtra(MainPresenter.EXTRA_NEW_MESSAGE, message);
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public boolean getTrackingStatus() {
        return isTracking;
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
        Intent contentIntent = MainActivity.newIntent(this);
        //contentIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(this, requestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingContentIntent);


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    public class LocalBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }
}
