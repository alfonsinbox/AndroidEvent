package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = ((GlobalApplication) this.getApplication()).getmClient();
        activity = this;

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
                    String token = new Gson().fromJson(response.getContent(), JsonObject.class).get("token").getAsString();
                    AuthUtilities.saveToken(token, activity);
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            AuthUtilities.refreshToken(activity, mClient, tokenRefreshedResponseCallback);
        } else {

            setContentView(R.layout.activity_sign_in);

            usernameEditText = (EditText) findViewById(R.id.edittext_username);
            passwordEditText = (EditText) findViewById(R.id.edittext_password);

        }

    }

    public void signIn(View view) {
        final Activity activity = this;

        String s = AuthUtilities.getLocalToken(activity);

        System.out.println("I FOUND A TOKEN! " + AuthUtilities.getLocalToken(activity));

        ServiceFilterResponseCallback serviceFilterResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }

                String token = new Gson().fromJson(response.getContent(), JsonObject.class).get("token").getAsString();

                try {
                    System.out.println("On Success");
                    System.out.println("TOKEN WAS RETRIEVED! " + token);
                    AuthUtilities.saveToken(token, activity);

                    if (!AuthUtilities.getLocalToken(activity).equals("")) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        // Finish activity so you can't navigate back to it
                        finish();
                    } else {

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        //authUtilities.requestToken(mClient, "admin", "password111", serviceFilterResponseCallback);
        AuthUtilities.requestToken(mClient, usernameEditText.getText().toString(), passwordEditText.getText().toString(), serviceFilterResponseCallback);
    }

    public void navigateCreateAccount(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }
}
