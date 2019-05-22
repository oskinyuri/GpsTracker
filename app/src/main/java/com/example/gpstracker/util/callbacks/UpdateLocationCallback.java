package com.example.gpstracker.util.callbacks;

public interface UpdateLocationCallback {

    void onResponse();

    void onFailure(Throwable throwable);
}
