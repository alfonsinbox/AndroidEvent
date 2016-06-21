package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Calendar;

public class ForgotPasswordActivity extends AppCompatActivity {

    MobileServiceClient mClient;
    RelativeLayout resetPasswordEmailButton;
    RelativeLayout resetPasswordEmailProgress;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mClient = GlobalApplication.getmClient();
        activity = this;
        resetPasswordEmailButton = (RelativeLayout) findViewById(R.id.reset_password_email_button);
        resetPasswordEmailProgress = (RelativeLayout) findViewById(R.id.reset_password_email_progress);
    }

    public void sendResetPasswordEmail(View view) {
        resetPasswordEmailButton.setVisibility(View.GONE);
        resetPasswordEmailProgress.setVisibility(View.VISIBLE);

        ServiceFilterResponseCallback resetPasswordEmailSentCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    resetPasswordEmailButton.setVisibility(View.VISIBLE);
                    resetPasswordEmailProgress.setVisibility(View.GONE);
                    return;
                }
                finish();
            }
        };

        String userEmail = ((EditText) findViewById(R.id.edittext_reset_password_email)).getText().toString();
        UserUtilities.sendResetPasswordEmail(activity, userEmail, mClient, resetPasswordEmailSentCallback);
    }
}
