package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

/**
 * Created by alfon on 2016-02-20.
 */
public class GlobalApplication extends Application {

    Activity activity;
    public MobileServiceClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mClient = new MobileServiceClient(
                    "https://theeventapp.azurewebsites.net/",
                    this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MobileServiceClient getmClient() {
        return mClient;
    }
}
