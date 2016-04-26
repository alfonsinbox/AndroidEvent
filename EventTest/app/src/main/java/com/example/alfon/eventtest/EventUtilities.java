package com.example.alfon.eventtest;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Pair;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-02-23.
 */
public class EventUtilities {
    public void getUserEvents(Activity activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", new AuthUtilities().getLocalToken(activity)));

        mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void getEventsForMap(Activity activity, String latitude, String longitude, String within_radius, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("latitude", latitude));
        params.add(Pair.create("longitude", longitude));
        params.add(Pair.create("within_radius", within_radius));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void getEventDetails(Activity activity, String eventId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("event_id", eventId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void searchEvents(Activity activity, String query, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void getMyEvents(Activity activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/my/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void getUpcomingEvents(Activity activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/upcoming/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static ListenableFuture<ServiceFilterResponse> searchEventsFuture (Activity activity, String query, MobileServiceClient mClient) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("query", query));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        return mClient.invokeApi("event/get", null, "POST", headers, params);
    }

    public void createEvent(Activity activity, String serializedEvent, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("serialized_event", serializedEvent));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/create", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void addHostsToEvent(Activity activity, String eventId, List<String> userIds, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("event_id", eventId));
        params.add(Pair.create("user_ids", TextUtils.join(",", userIds)));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/hosts/add", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void addAttendantToEvent(Activity activity, String eventId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("event_id", eventId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/attendants/add", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public void removeAttendantFromEvent(Activity activity, String eventId, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(Pair.create("event_id", eventId));

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(activity)));

        mClient.invokeApi("event/attendants/remove", null, "POST", headers, params, serviceFilterResponseCallback);
    }
}
