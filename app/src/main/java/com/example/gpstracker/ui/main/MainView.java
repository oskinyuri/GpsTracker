package com.example.gpstracker.ui.main;

import android.app.Activity;

public interface MainView {

    void setCarNumber(String carNumber);

    void setServiceStatus(String status);

    void setMessage(String message);

    void updateButtonUI(int buttonColor);

    Activity getViewActivity();

}
