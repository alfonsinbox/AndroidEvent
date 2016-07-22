package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by alfon on 2016-04-15.
 */
public class NotificationUtilities {
    public static void registerGcmId(final Context activity, final String gcmRegistrationId, MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {
        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("gcm_registration_id", gcmRegistrationId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_REFRESH_TOKEN, activity)));

                mClient.invokeApi("notification/register/google", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();
    }

    public static void deleteRegistration(final Context activity, final String registrationId, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback){
        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("registration_id", registrationId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("notification/delete", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();
    }

    public static void setDeviceRegisteredForNotifications(Context activity, boolean valueToPut){
        SharedPreferences storedUserPreferences = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storedUserPreferences.edit();
        editor.putBoolean(GlobalApplication.PREFERENCE_DEVICE_REGISTERED_FOR_PUSH, valueToPut);
        editor.commit();
    }
    public static boolean isDeviceRegistered(Context activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getBoolean(GlobalApplication.PREFERENCE_DEVICE_REGISTERED_FOR_PUSH, false);
    }

    public static void setRegistrationId(Context activity, String registrationId){
        SharedPreferences storedUserPreferences = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = storedUserPreferences.edit();
        editor.putString(GlobalApplication.PREFERENCE_REGISTRATION_ID, registrationId);
        editor.commit();
    }
    public static String getRegistrationId(Context activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getString(GlobalApplication.PREFERENCE_REGISTRATION_ID, "");
    }
    public static String getGcmRegistrationId(Context activity){
        return activity
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");
    }
}
