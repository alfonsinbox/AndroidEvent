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
import com.soundcloud.android.crop.Crop;

/**
 * Created by alfon on 2016-02-20.
 */
public class GlobalApplication extends Application {


    public static MobileServiceClient mClient;
    //public static final String mClient;
    public static final String SENDER_ID = "345618100514";
    public static final String PREFERENCES_USERSETTINGS = "UserSettings";
    public static final String PREFERENCE_GCM_REGISTRATION_ID = "PreferenceGcmRegId";
    public static final String PREFERENCE_REGISTRATION_ID = "PreferenceRegId";
    public static final String PREFERENCE_DEVICE_REGISTERED_FOR_PUSH = "PreferenceDeviceRegisteredForPush";
    public static final String PREFERENCE_USER_REFRESH_TOKEN = "refresh_token";
    public static final String PREFERENCE_USER_ACCESS_TOKEN = "access_token";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_EXPIRES = "access_token_expires";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_VALID_FROM = "access_token_valid_from";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_VALID_TO = "access_token_valid_to";
    public static final String EXTRA_EVENTDETAILS_FETCH_DATA = "EventDetailsFetchData";
    public static final String EXTRA_EVENTDETAILS_EVENT_ID = "EventDetailsEventId";
    public static final String EXTRA_USEROVERVIEW_USER_ID = "UserOverviewUserId";
    public static final String EXTRA_USEROVERVIEW_FETCH_DATA = "UserOverviewUserId";
    public static final String EXTRA_CANCELEVENT_EVENTID = "CancelEventEventId";
    public static final String ACTION_TOKEN_CHECK_CALLBACK_SUCCESS = "ActionTokenCheckCallbackSuccess";
    public static final String ACTION_TOKEN_CHECK_CALLBACK_FAILURE = "ActionTokenCheckCallbackFailure";
    public static final int REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE = 10;
    public static final int REQUEST_CODE_CAMERA_INTENT_CROP_PICTURE = Crop.REQUEST_CROP;//11;



    public static final String NOTIFICATION_EXTRA_ACTIVITY_REDIRECTION = "NotificationActivityRedirect";

    public static final Event currentlyDetailedEvent = null;

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
