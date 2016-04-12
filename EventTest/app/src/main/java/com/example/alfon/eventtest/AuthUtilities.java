package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-02-18.
 */
public class AuthUtilities {

    public static void requestToken(MobileServiceClient mClient, String username, String password, ServiceFilterResponseCallback serviceFilterResponseCallback) {

        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("username", username));
        params.add(Pair.create("password", password));

        List<Pair<String, String>> headers = new ArrayList<>();

        mClient.invokeApi("auth/token/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void refreshToken(Activity activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", getLocalToken(activity)));

        mClient.invokeApi("auth/token/refresh", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static Boolean saveToken(String token, Activity activity) {
        try {
            SharedPreferences storedUserData = activity.getSharedPreferences("UserSettings", 0);
            SharedPreferences.Editor editor = storedUserData.edit();
            editor.putString("token", token);
            editor.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public static Boolean removeToken(Activity activity) {
        try {
            SharedPreferences storedUserData = activity.getSharedPreferences("UserSettings", 0);
            SharedPreferences.Editor editor = storedUserData.edit();
            editor.putString("token", "");
            editor.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String getLocalToken(Activity activity) {
        return activity.getSharedPreferences("UserSettings", 0).getString("token", "");
    }
}
