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
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfon on 2016-02-23.
 */
public class UserUtilities {
    public void getUserInfo(final Context activity, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("user/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    /*public void getUsersFromString(final Context activity, final String query, MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback) {

        TokenCheck tokenCheck = new TokenCheck(activity, mClient) {
            @Override
            public void onSuccess() {
                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("user/get", null, "POST", headers, params, serviceFilterResponseCallback);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        tokenCheck.checkToken();
    }*/

    public void getUsersFromStringFuture(final Context activity, final String query, final MobileServiceClient mClient, final ListenableFutureCreatedCallback listenableFutureCreatedCallback) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("query", query));

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

    public static void createUser(final Context activity, final String serializedUser, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("serialized_user", serializedUser));
                params.add(Pair.create("device", android.os.Build.MODEL));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("user/create", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static void setInterests(final Context activity, final List<String> categoryIds, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback){
        {

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    activity.unregisterReceiver(this);

                    List<Pair<String, String>> params = new ArrayList<>();
                    params.add(Pair.create("category_ids", TextUtils.join(",", categoryIds)));

                    List<Pair<String, String>> headers = new ArrayList<>();
                    headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                    mClient.invokeApi("user/interests/set", null, "POST", headers, params, serviceFilterResponseCallback);
                }
            };
            IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
            activity.registerReceiver(receiver, intentFilter);
            Intent serviceIntent = new Intent(activity, TokenCheckService.class);
            activity.startService(serviceIntent);
        }
    }

    public static void sendResetPasswordEmail(final Context activity, final String email, final MobileServiceClient mClient, final ServiceFilterResponseCallback serviceFilterResponseCallback){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.unregisterReceiver(this);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(Pair.create("email", email));

                List<Pair<String, String>> headers = new ArrayList<>();
                headers.add(Pair.create("X-ZUMO-AUTH", AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity)));

                mClient.invokeApi("user/password/reset", null, "POST", headers, params, serviceFilterResponseCallback);
            }
        };
        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
        activity.registerReceiver(receiver, intentFilter);
        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
        activity.startService(serviceIntent);
    }

    public static class UploadProfilePictureTask extends AsyncTask<Void, Void, Boolean> {
        private final Context _activity;
        private final Uri _imageUri;
        private final AsyncTaskCallback _asyncTaskCallback;

        public UploadProfilePictureTask(Context activity, Uri imageUri, AsyncTaskCallback asyncTaskCallback){
            _activity = activity;
            _imageUri = imageUri;
            _asyncTaskCallback = asyncTaskCallback;
        }

        @Override
        protected Boolean doInBackground(Void... values) {
            String requestURL = "https://theeventapp.azurewebsites.net/api/user/picture/set?ZUMO-API-VERSION=2.0.0";
            String charset = "UTF-8";
            try {

                MultipartUtility multipart = new MultipartUtility(_activity, requestURL, charset);

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
