package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class SignInActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    EditText loginNameEditText;
    EditText passwordEditText;
    String gcmRegistrationId;

    RelativeLayout signInButton;
    RelativeLayout signingInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = GlobalApplication.getmClient();
        activity = this;

        // Check if user is logged in
        if (!AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_REFRESH_TOKEN, activity).equals("")) {

            System.out.println("User has previously logged in");
            // Check if user has access_token from before and the it hasn't expired yet
            if (!AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity).equals("")
                    && AuthUtilities.accessTokenValidTimeLeft(activity) > 60) {

                System.out.println("Access token exists and is valid");
                // Renew access_token
                ServiceFilterResponseCallback renewedAccessTokenResponseCallback = new ServiceFilterResponseCallback() {
                    @Override
                    public void onResponse(ServiceFilterResponse response, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        AccessToken accessToken = new Gson().fromJson(response.getContent(), AccessToken.class);
                        AuthUtilities.saveAccessToken(accessToken.token, accessToken.expires, activity);

                        navigateCorrectActivityFinishThis();
                    }
                };
                AuthUtilities.renewAccessToken(activity, mClient, renewedAccessTokenResponseCallback);
            } else {

                System.out.println("No valid access token");
                // Get access_token from refresh_token
                ServiceFilterResponseCallback refreshedAccessTokenResponseCallback = new ServiceFilterResponseCallback() {
                    @Override
                    public void onResponse(ServiceFilterResponse response, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        AccessToken accessToken = new Gson().fromJson(response.getContent(), AccessToken.class);
                        AuthUtilities.saveAccessToken(accessToken.token, accessToken.expires, activity);
                        String a = AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_ACCESS_TOKEN, activity);
                        System.out.println(a);

                        navigateCorrectActivityFinishThis();
                    }
                };
                AuthUtilities.refreshAccessToken(activity, mClient, refreshedAccessTokenResponseCallback);
            }
        } else {

            System.out.println("User has not logged in");
            //User was not logged in
            setContentView(R.layout.activity_sign_in);

            loginNameEditText = (EditText) findViewById(R.id.edittext_login_name);
            passwordEditText = (EditText) findViewById(R.id.edittext_password);

            signInButton = (RelativeLayout) findViewById(R.id.button_sign_in);
            signingInProgress = (RelativeLayout) findViewById(R.id.signing_in_progress);

            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        signIn(v);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /*public void deleteCurrentNotificationRegistration() {
        final ServiceFilterResponseCallback deletedNotificationRegistrationResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    deleteCurrentNotificationRegistration();
                    return;
                }
                NotificationUtilities.setDeviceRegisteredForNotifications(activity, false);
            }
        };
        String registrationId = NotificationUtilities.getRegistrationId(activity);
        NotificationUtilities.deleteRegistration(activity, registrationId, mClient, deletedNotificationRegistrationResponseCallback);
    }*/

    public void signIn(View view) {
        signInButton.setVisibility(View.INVISIBLE);
        signingInProgress.setVisibility(View.VISIBLE);

        System.out.println("Signing in");

        final ServiceFilterResponseCallback finishedNotificationRegistrationResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                System.out.println("Returned from registering for push");
                if (exception != null) {
                    exception.printStackTrace();
                    /*signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity, R.string.could_not_sign_in_toast, Toast.LENGTH_SHORT).show();
                    return;*/
                }

                Toast.makeText(activity, R.string.device_registered_push, Toast.LENGTH_SHORT).show();

                String registrationId = new Gson().fromJson(response.getContent(), JsonObject.class).get("registration_id").getAsString();

                System.out.println(response.getContent());

                NotificationUtilities.setRegistrationId(activity, registrationId);

                NotificationUtilities.setDeviceRegisteredForNotifications(activity, true);

                // If both requests went well, user is logged in
                navigateCorrectActivityFinishThis();
            }
        };

        ServiceFilterResponseCallback gotRefreshTokenResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                System.out.println("Returned from getting refresh token");
                if (exception != null) {
                    exception.printStackTrace();
                    signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity, R.string.could_not_sign_in_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                RefreshToken refreshToken = new Gson().fromJson(response.getContent(), RefreshToken.class);

                try {
                    System.out.println("TOKEN WAS RETRIEVED: " + refreshToken.token);
                    AuthUtilities.saveRefreshToken(refreshToken.token, activity);

                    if (!AuthUtilities.getLocalToken(GlobalApplication.PREFERENCE_USER_REFRESH_TOKEN, activity).equals("")) {
                        // Register device for push notifications
                        gcmRegistrationId = activity
                                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                                .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");

                        NotificationUtilities.registerGcmId(activity, gcmRegistrationId, GlobalApplication.mClient, finishedNotificationRegistrationResponseCallback);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        AuthUtilities.requestRefreshToken(mClient, loginNameEditText.getText().toString(),
                passwordEditText.getText().toString(), gotRefreshTokenResponseCallback);
    }

    private void navigateCorrectActivityFinishThis() {
        Intent intent;
        TaskStackBuilder stackBuilder;
        String activityRedirect;

        try {
            activityRedirect = getIntent().getExtras().getString(GlobalApplication.NOTIFICATION_EXTRA_ACTIVITY_REDIRECTION);
        } catch (Exception e) {
            activityRedirect = "";
        }

        if (activityRedirect == null) {
            intent = new Intent(activity, MainActivity.class);
            stackBuilder = TaskStackBuilder.create(this)
                    .addNextIntent(intent);
        } else {
            switch (activityRedirect) {
                case "UserOverview":
                    intent = new Intent(activity, UserOverviewActivity.class);
                    intent.putExtra(GlobalApplication.EXTRA_USEROVERVIEW_FETCH_DATA,
                            getIntent().getExtras().getBoolean(GlobalApplication.EXTRA_USEROVERVIEW_FETCH_DATA));
                    intent.putExtra(GlobalApplication.EXTRA_USEROVERVIEW_USER_ID,
                            getIntent().getExtras().getString(GlobalApplication.EXTRA_USEROVERVIEW_USER_ID));
                    stackBuilder = TaskStackBuilder.create(this)
                            .addParentStack(UserOverviewActivity.class)
                            .addNextIntent(intent);
                    break;
                case "EventDetails":
                    intent = new Intent(activity, EventDetailsActivity.class);
                    intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA,
                            getIntent().getExtras().getBoolean(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA));
                    intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID,
                            getIntent().getExtras().getString(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID));
                    stackBuilder = TaskStackBuilder.create(this)
                            .addParentStack(EventDetailsActivity.class)
                            .addNextIntent(intent);
                    break;
                default:
                    intent = new Intent(activity, MainActivity.class);
                    stackBuilder = TaskStackBuilder.create(this)
                            .addNextIntent(intent);
                    break;
            }
        }
        //startActivity(intent);
        stackBuilder.startActivities();

        // Finish activity so you can't navigate back to it
        finish();
    }

    public void navigateForgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void navigateCreateAccount(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

}
