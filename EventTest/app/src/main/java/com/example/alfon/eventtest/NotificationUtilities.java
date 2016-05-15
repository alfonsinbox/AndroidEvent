package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-04-15.
 */
public class NotificationUtilities {
    public static void registerGcmId(Context context, String gcmRegistrationId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("gcm_registration_id", gcmRegistrationId));
        //params.add(Pair.create("tags", ""));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(context)));

        mClient.invokeApi("notification/register/google", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void deleteRegistration(Activity activity, String registrationId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("registration_id", registrationId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("notification/delete", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void setDeviceRegisteredForNotifications(Activity activity, boolean valueToPut){
        SharedPreferences storedUserPreferences = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storedUserPreferences.edit();
        editor.putBoolean(GlobalApplication.PREFERENCE_DEVICE_REGISTERED_FOR_PUSH, valueToPut);
        editor.commit();
    }
    public static boolean isDeviceRegistered(Activity activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getBoolean(GlobalApplication.PREFERENCE_DEVICE_REGISTERED_FOR_PUSH, false);
    }

    public static void setRegistrationId(Activity activity, String registrationId){
        SharedPreferences storedUserPreferences = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storedUserPreferences.edit();
        editor.putString(GlobalApplication.PREFERENCE_REGISTRATION_ID, registrationId);
        editor.commit();
    }
    public static String getRegistrationId(Activity activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getString(GlobalApplication.PREFERENCE_REGISTRATION_ID, "");
    }
    public static String getGcmRegistrationId(Activity activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");
    }
}
