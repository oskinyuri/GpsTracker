package com.example.gpstracker.ui.main;

import android.app.Activity;

public interface MainView {

    void setCarNumber(String carNumber);

    void setServiceStatus(String status);

    Activity getViewActivity();

}
