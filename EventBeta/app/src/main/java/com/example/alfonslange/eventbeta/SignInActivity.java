package com.example.alfonslange.eventbeta;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import helpers.AuthUtilities;
import objects.Globals;
import objects.RefreshToken;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final Activity a = this;
        AuthUtilities.requestRefreshToken(Globals.mClient, "", "", new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                AuthUtilities.saveRefreshToken(new Gson().fromJson(response.getContent(), RefreshToken.class).token, a);
                startActivity(new Intent(a, MainActivity.class));
            }
        });
    }
}
