package com.example.gpstracker;

import android.app.Application;

import com.example.gpstracker.util.NotificationUtils;

public class GpsTracker extends Application {

    private NotificationUtils mNotificationUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationUtils = new NotificationUtils(getApplicationContext());
        mNotificationUtils.createNotificationChannel();
    }
}
