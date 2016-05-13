package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class ConfirmationInformationActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    RelativeLayout buttonAccountConfirmed;
    RelativeLayout progressAccountConfirmed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_information);
        activity = this;
        mClient = GlobalApplication.getmClient();

        buttonAccountConfirmed = (RelativeLayout)findViewById(R.id.confirmed_account_button);
        progressAccountConfirmed = (RelativeLayout)findViewById(R.id.confirmed_account_progress);
    }

    public void continueToAppAndRegisterForPush(View view){

        buttonAccountConfirmed.setVisibility(View.GONE);
        progressAccountConfirmed.setVisibility(View.VISIBLE);

        ServiceFilterResponseCallback finishedRegisterForPushResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    buttonAccountConfirmed.setVisibility(View.VISIBLE);
                    progressAccountConfirmed.setVisibility(View.GONE);
                    exception.printStackTrace();
                    return;
                }
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
            }
        };

        NotificationUtilities.registerGcmId(activity, activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, MODE_PRIVATE).getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, ""),
                mClient, finishedRegisterForPushResponseCallback);
    }
}
