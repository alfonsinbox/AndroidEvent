package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Pair;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-03-28.
 */
public class CategoryUtilities {
    public static void getCategories(final Context activity, final String query, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("category/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void getCategoriesFuture(final Context activity, final String query, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                listenableFutureCreatedCallback.onSuccess(mClient.invokeApi("category/get", null, "POST", headers, params));
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void getAllCategoriesFuture(final Context activity, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                listenableFutureCreatedCallback.onSuccess(mClient.invokeApi("user/get", null, "POST", headers, params));
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }
}
