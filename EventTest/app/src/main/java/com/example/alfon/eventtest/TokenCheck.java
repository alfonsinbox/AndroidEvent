package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

/**
 * Created by alfon on 2016-07-18.
 */
public abstract class TokenCheck implements TokenCheckCallback {

    Context context;
    MobileServiceClient mClient;

    public TokenCheck(Context context, MobileServiceClient mClient) {
        this.mClient = mClient;
        this.context = context;
    }

    public void checkToken() {

        System.out.println("STARTING TOKEN VALIDATION CHECK");

        int validTimeLeft = AuthUtilities.accessTokenValidTimeLeft(context);

        // Check if user has access_token from before and the it hasn't expired yet
        if (!AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, context).equals("")
                && validTimeLeft > 60) {

            System.out.println("Access token exists and is valid, expires in " + validTimeLeft + " seconds");
            onSuccess();
        } else {

            /** Probably bad practice to be able to refresh the access token,
              * it makes it easier to gain control over someone's account */
            /*
            // Check if token still has at least ten seconds left
            if (validTimeLeft > 10) {

                System.out.println("Trying to renew Access token");
                // Renew token
                ServiceFilterResponseCallback renewedAccessTokenResponseCallback = new ServiceFilterResponseCallback() {
                    @Override
                    public void onResponse(ServiceFilterResponse response, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                            onFailure(exception);
                            return;
                        }
                        AccessToken accessToken = new Gson().fromJson(response.getContent(), AccessToken.class);
                        AuthUtilities.saveAccessToken(accessToken.token, accessToken.expires, activity);

                        onSuccess();
                    }
                };
                AuthUtilities.renewAccessToken(activity, mClient, renewedAccessTokenResponseCallback );
            } else {*/

                System.out.println("No valid access token, refreshing with Refresh token");
                // Get access_token from refresh_token
                ServiceFilterResponseCallback refreshedAccessTokenResponseCallback = new ServiceFilterResponseCallback() {
                    @Override
                    public void onResponse(ServiceFilterResponse response, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                            onFailure(exception);
                            return;
                        }
                        AccessToken accessToken = new Gson().fromJson(response.getContent(), AccessToken.class);
                        AuthUtilities.saveAccessToken(accessToken.token, accessToken.expires, context);

                        onSuccess();
                    }
                };
                AuthUtilities.refreshAccessToken(context, mClient, refreshedAccessTokenResponseCallback);
            //}
        }
    }
}
