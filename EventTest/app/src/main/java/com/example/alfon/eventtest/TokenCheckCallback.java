package com.example.alfon.eventtest;

/**
 * Created by alfon on 2016-07-18.
 */
public interface TokenCheckCallback {
    void onSuccess();
    void onFailure(Exception exception);
}
