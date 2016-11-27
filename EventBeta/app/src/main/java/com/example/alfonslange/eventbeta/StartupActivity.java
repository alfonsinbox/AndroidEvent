package com.example.alfonslange.eventbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

import helpers.AuthUtilities;
import helpers.TokenCheck;
import objects.Globals;

/**
 * Created by alfonslange on 18/09/16.
 */
public class StartupActivity extends AppCompatActivity {
    private static final String TAG = "StartupActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: JUST CREATING THE DAMN ACTIVITY");

        // TODO Check token???
        try {
            Globals.setmClient(new MobileServiceClient(
                    "https://theeventapp.azurewebsites.net/",
                    this));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TokenCheck.setContext(this);

        //TODO REMOVE THIS SHIT
        AuthUtilities.saveAccessToken("", -10, this);

        Log.d(TAG, "onCreate: " + AuthUtilities.getLocalToken(Globals.PREFERENCE_USER_REFRESH_TOKEN, this));
        if (AuthUtilities.getLocalToken(Globals.PREFERENCE_USER_REFRESH_TOKEN, this).equals("")) {
            startActivity(new Intent(this, SignInActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
