package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import android.location.Location;
import android.util.Pair;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-02-27.
 */
public class LocationUtilities extends Activity {

    // Will get locations from a query and lat/lon
    public static void getLocationSuggestions(final Context activity, final String query, final String latitude, final String longitude, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("location/suggestions", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    // Will get locations from the JSON token passed and a lat/lon
    public static void getLocationSuggestions(final Context activity, final String latitude, final String longitude, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("location/suggestions", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    // Will get locations from the JWT passed and a lat/lon
    public static void getLocationSuggestionsFuture(final Context activity, final String latitude, final String longitude, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                listenableFutureCreatedCallback.onSuccess(mClient.invokeApi("location/suggestions", null, "POST", headers, params));
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    // Will get locations from a query and lat/lon
    public static void getLocationSuggestionsFuture(final Context activity, final String query, final String latitude, final String longitude, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                listenableFutureCreatedCallback.onSuccess(mClient.invokeApi("location/suggestions", null, "POST", headers, params));
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public void createLocation(final Context activity, final String name, final String latitude, final String longitude, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("name", name));
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("location/create", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void getLocationDetails(final Context activity, final String placeId, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("place_id", placeId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("location/details", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static android.location.Location getUserLocation(Context activity) throws Exception {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        // boolean a = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        // boolean b = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        // System.out.println(provider + a + b);

        Location userLocation = locationManager.getLastKnownLocation(provider);

        if (userLocation == null) {
            throw new Exception("Couldn't get location");
        }
        System.out.println(String.format("%s, %s", String.valueOf(userLocation.getLatitude()), String.valueOf(userLocation.getLongitude())));
        return userLocation;
    }
}