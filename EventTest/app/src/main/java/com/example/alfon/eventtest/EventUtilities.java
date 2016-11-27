package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Pair;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.repacked.antlr.runtime.Token;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-02-23.
 */
public class EventUtilities {
    public static void getUserEvents(final Context activity, MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {
        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();

    }

    public static void getEventsForMap(final Context activity, final String latitude, final String longitude, final String within_radius, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("latitude", latitude));
                params.add(Pair.create("longitude", longitude));
                params.add(Pair.create("within_radius", within_radius));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);

    }

    public static void getEventDetails(final Context activity, final String eventId, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("event_id", eventId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void searchEvents(final Context activity, final String query, MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();
    }

    public static void getMyEvents(final Context activity, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/my/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void getUpcomingEvents(final Context activity, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/upcoming/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void searchEventsFuture(final Context activity, final String query, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                listenableFutureCreatedCallback.onSuccess(mClient.invokeApi("event/get", null, "POST", headers, params));
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void createEvent(final Context activity, final String serializedEvent, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("serialized_event", serializedEvent));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/create", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void addHostsToEvent(final Context activity, final String eventId, final List<String> userIds, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("event_id", eventId));
                params.add(Pair.create("user_ids", TextUtils.join(",", userIds)));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/hosts/add", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void addAttendantToEvent(final Context activity, final String eventId, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("event_id", eventId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/attendants/add", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void removeAttendantFromEvent(final Context activity, final String eventId, MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {
        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("event_id", eventId));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/attendants/remove", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();
    }

    public static void cancelEvent(final Context activity, final String eventId, final String canceledReason, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("event_id", eventId));
                params.add(Pair.create("canceled_reason", canceledReason));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("event/attendants/remove", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static class UploadEventImageTask extends AsyncTask<Void, Void, Boolean> {
        private final Activity _activity;
        private final Uri _imageUri;
        private final AsyncTaskCallback _asyncTaskCallback;
        private final Event _detailedEvent;

        public UploadEventImageTask(Activity activity, Uri imageUri, Event detailedEvent, AsyncTaskCallback asyncTaskCallback) {
            _activity = activity;
            _imageUri = imageUri;
            _detailedEvent = detailedEvent;
            _asyncTaskCallback = asyncTaskCallback;
        }

        @Override
        protected Boolean doInBackground(Void... values) {
            String requestURL = "https://theeventapp.azurewebsites.net/api/event/picture/set?ZUMO-API-VERSION=2.0.0";
            String charset = "UTF-8";
            try {
                MultipartUtility multipart = new MultipartUtility(_activity, requestURL, charset);

                multipart.addFormField("event_id", _detailedEvent.id);

                File image = FileUtilities.getImage(_imageUri);
                multipart.addFilePart("file", image);

                String completeResponse = "";
                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                for (String line : response) {
                    completeResponse += line;
                    System.out.println("Upload Files Response:::" + line);
                }
                System.out.println(completeResponse);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            _asyncTaskCallback.asyncTaskCallbackDone(success);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
