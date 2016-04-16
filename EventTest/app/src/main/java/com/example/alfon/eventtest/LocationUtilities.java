package com.example.alfon.eventtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
    public static void getLocationSuggestions(Activity activity, String query, String latitude, String longitude, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("location/suggestions", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    // Will get locations from the JSON token passed and a lat/lon
    public static void getLocationSuggestions(Activity activity, String latitude, String longitude, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("location/suggestions", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    // Will get locations from the JSON token passed and a lat/lon
    public static ListenableFuture<ServiceFilterResponse> getLocationSuggestionsFuture(Activity activity, String latitude, String longitude, MobileServiceClient mClient) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("location/suggestions", null, "POST", headers, params);
    }

    // Will get locations from a query and lat/lon
    public static ListenableFuture<ServiceFilterResponse> getLocationSuggestionsFuture(Activity activity, String query, String latitude, String longitude, MobileServiceClient mClient) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("location/suggestions", null, "POST", headers, params);
    }

    public void createLocation(Activity activity, String name, String latitude, String longitude, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("name", name));
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("location/create", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void getLocationDetails(Activity activity, String placeId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("place_id", placeId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("location/details", null, "POST", headers, params, serviceFilterResponseCallback);

    }

    public static android.location.Location getUserLocation(Activity activity) throws Exception {
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