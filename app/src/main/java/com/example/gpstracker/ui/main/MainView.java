package com.example.gpstracker.ui.main;

import android.app.Activity;

public interface MainView {

    void setCarNumber(String carNumber);

    void setServiceStatus(String status);

    void setMessage(String message);

    void toAlert();

    void fromAlert();

    Activity getViewActivity();

    void setAlarmButtonEnabled(boolean enabled);

    void setStartButtonEnabled(boolean enabled);

    void setStopButtonEnabled(boolean enabled);

    void setImageTrackingIsOff();

    void setImageTrackingIsOn();

    void setCarNumberEnabled(boolean enabled);

    void setAlertButtonVisible(boolean visible);
}
