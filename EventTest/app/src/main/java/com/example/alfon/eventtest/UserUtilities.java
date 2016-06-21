package com.example.alfon.eventtest;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Pair;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by alfon on 2016-02-23.
 */
public class UserUtilities {
    public void getUserInfo(Activity activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("user/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void getUsersFromString(Activity activity, String query, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("user/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public ListenableFuture<ServiceFilterResponse> getUsersFromStringFuture(Activity activity, String query, MobileServiceClient mClient) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("user/get", null, "POST", headers, params);
    }

    public static void createUser(Activity activity, String serializedUser, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("serialized_user", serializedUser));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("user/create", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void setInterests(Activity activity, List<String> categoryIds, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("category_ids", TextUtils.join(",", categoryIds)));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("user/interests/set", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void sendResetPasswordEmail(Activity activity, String email, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("email", email));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("user/password/reset", null, "POST", headers, params, serviceFilterResponseCallback);
    }
}
