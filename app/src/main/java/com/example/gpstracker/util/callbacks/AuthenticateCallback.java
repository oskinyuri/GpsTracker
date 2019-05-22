package com.example.gpstracker.util.callbacks;

public interface AuthenticateCallback {
    void onResponse();

    void onFailure(Throwable throwable);
}
