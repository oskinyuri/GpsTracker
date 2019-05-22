package com.example.gpstracker.util.callbacks;

public interface UpdateMessageCallback {
    void onResponse(String message);

    void onFailure(Throwable throwable);
}
