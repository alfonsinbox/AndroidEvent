package com.example.alfon.eventtest;

import android.app.Activity;
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
    public static void getCategories(Activity activity, String query, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("category/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static ListenableFuture<ServiceFilterResponse> getCategoriesFuture(Activity activity, String query, MobileServiceClient mClient){
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("category/get", null, "POST", headers, params);
    }

    public static ListenableFuture<ServiceFilterResponse> getAllCategoriesFuture(Activity activity, MobileServiceClient mClient){
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("category/get", null, "POST", headers, params);
    }
}
