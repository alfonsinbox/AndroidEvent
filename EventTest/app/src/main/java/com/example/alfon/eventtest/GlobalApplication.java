package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.notifications.NotificationsManager;

/**
 * Created by alfon on 2016-02-20.
 */
public class GlobalApplication extends Application {

    Activity activity;
    public static MobileServiceClient mClient;
    public static final String SENDER_ID = "345618100514";

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

        NotificationsManager.handleNotifications(this, SENDER_ID, MyNotificationsHandler.class);
    }

    public static MobileServiceClient getmClient() {
        return mClient;
    }
}
