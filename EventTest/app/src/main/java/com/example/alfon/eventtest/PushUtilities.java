package com.example.alfon.eventtest;

import android.app.Activity;
import android.util.Pair;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-04-15.
 */
public class PushUtilities {
    public static void registerGcmId(Activity activity, String gcmRegistrationId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("gcm_registration_id", gcmRegistrationId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("notification/register", null, "POST", headers, params, serviceFilterResponseCallback);
    }
}
