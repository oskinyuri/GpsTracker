package com.example.gpstracker.ui;

public  interface AuthenticateCallback {
    void onResponse();

    //TODO add exception as parameter
    void onFailure(Throwable throwable);
}
