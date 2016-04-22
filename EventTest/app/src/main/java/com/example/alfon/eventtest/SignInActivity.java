package com.example.alfon.eventtest;

import android.*;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class SignInActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    EditText usernameEditText;
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
        if (!AuthUtilities.getLocalToken(activity).equals("")) {
            ServiceFilterResponseCallback tokenRefreshedResponseCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    if (exception != null) {
                        setContentView(R.layout.activity_sign_in);
                        usernameEditText = (EditText) findViewById(R.id.edittext_username);
                        passwordEditText = (EditText) findViewById(R.id.edittext_password);
                        return;
                    }
                    String token = new Gson().fromJson(response.getContent(), JsonObject.class).get(GlobalApplication.PREFERENCE_USER_TOKEN).getAsString();
                    AuthUtilities.saveToken(token, activity);
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            // Refresh token
            AuthUtilities.refreshToken(activity, mClient, tokenRefreshedResponseCallback);
        } else {
            setContentView(R.layout.activity_sign_in);

            usernameEditText = (EditText) findViewById(R.id.edittext_username);
            passwordEditText = (EditText) findViewById(R.id.edittext_password);

            signInButton = (RelativeLayout) findViewById(R.id.button_sign_in);
            signingInProgress = (RelativeLayout) findViewById(R.id.signing_in_progress);
        }
    }

    public void signIn(View view) {
        System.out.println("PREVIOUS TOKEN: " + AuthUtilities.getLocalToken(activity));

        signInButton.setVisibility(View.INVISIBLE);
        signingInProgress.setVisibility(View.VISIBLE);

        final ServiceFilterResponseCallback finishedNotificationRegistrationResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    return;
                }

                String registrationId = new Gson().fromJson(response.getContent(), JsonObject.class).get("registration_id").getAsString();

                System.out.println(response.getContent());
                SharedPreferences storedUserPreferences = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = storedUserPreferences.edit();
                editor.putString(GlobalApplication.PREFERENCE_REGISTRATION_ID, registrationId);
                editor.commit();

                // If both requests went well, user is logged in
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                // Finish activity so you can't navigate back to it
                finish();
            }
        };

        ServiceFilterResponseCallback gotUserTokenResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    return;
                }

                String token = new Gson().fromJson(response.getContent(), JsonObject.class).get("token").getAsString();

                try {
                    System.out.println("TOKEN WAS RETRIEVED: " + token);
                    AuthUtilities.saveToken(token, activity);

                    if (!AuthUtilities.getLocalToken(activity).equals("")) {

                        // Register device for push notifications
                        gcmRegistrationId = activity
                                .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                                .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");

                        NotificationUtilities.registerGcmId(activity.getApplicationContext(), gcmRegistrationId, GlobalApplication.mClient, finishedNotificationRegistrationResponseCallback);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        AuthUtilities.requestToken(mClient, usernameEditText.getText().toString(), passwordEditText.getText().toString(), gotUserTokenResponseCallback);
    }

    public void navigateCreateAccount(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }
}
