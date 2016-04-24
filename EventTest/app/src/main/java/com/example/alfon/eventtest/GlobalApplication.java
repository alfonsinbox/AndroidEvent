package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceApplication;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.MobileServiceConnection;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.notifications.NotificationsManager;

/**
 * Created by alfon on 2016-02-20.
 */
public class GlobalApplication extends Application {


    public static MobileServiceClient mClient;
    public static final String SENDER_ID = "345618100514";
    public static final String PREFERENCES_USERSETTINGS = "UserSettings";
    public static final String PREFERENCE_GCM_REGISTRATION_ID = "PreferenceGcmRegId";
    public static final String PREFERENCE_REGISTRATION_ID = "PreferenceRegId";
    public static final String PREFERENCE_USER_TOKEN = "token";
    public static final String EXTRA_EVENTDETAILS_FETCH_DATA = "EventDetailsFetchData";
    public static final String EXTRA_EVENTDETAILS_EVENT_ID = "EventDetailsEventId";
    public static final String EXTRA_USEROVERVIEW_USER_ID = "UserOverviewUserId";
    public static final String EXTRA_USEROVERVIEW_FETCH_DATA = "UserOverviewUserId";

    public static final String NOTIFICATION_EXTRA_ACTIVITY_REDIRECTION = "NotificationActivityRedirect";

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

        // TODO Gör detta vid Sign In!!!!
        //if (!getSharedPreferences(PREFERENCES_USERSETTINGS, MODE_PRIVATE).getString(PREFERENCE_USER_TOKEN, "").equals("")){
        //    NotificationUtilities.registerDeviceForPush(this.getApplicationContext());
        //}

        NotificationsManager.handleNotifications(this, SENDER_ID, MyNotificationsHandler.class);
    }

    public static MobileServiceClient getmClient() {
        return mClient;
    }
}
