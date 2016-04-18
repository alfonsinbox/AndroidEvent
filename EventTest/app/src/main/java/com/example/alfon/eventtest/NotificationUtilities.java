package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Pair;

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
        params.add(Pair.create("tags", ""));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(context)));

        mClient.invokeApi("notification/register/google", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void a(final Context context) {

        final String gcmRegistrationId = context
                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");

        ServiceFilterResponseCallback finishedRegisterResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }
                System.out.println(response.getContent());
                String registrationId = response.getContent();
                SharedPreferences storedUserPreferences = context.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = storedUserPreferences.edit();
                editor.putString(GlobalApplication.PREFERENCE_REGISTRATION_ID, registrationId);
                editor.commit();
            }
        };

        NotificationUtilities.registerGcmId(context, gcmRegistrationId, GlobalApplication.mClient, finishedRegisterResponseCallback);

    }
}
