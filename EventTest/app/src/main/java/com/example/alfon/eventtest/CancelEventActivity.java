package com.example.alfon.eventtest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class CancelEventActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    String eventId;

    RelativeLayout cancelEventButton;
    RelativeLayout cancelEventProgress;
    EditText canceledReasonEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_event);

        activity = this;
        mClient = GlobalApplication.getmClient();

        eventId = this.getIntent().getExtras().getString(GlobalApplication.EXTRA_CANCELEVENT_EVENTID);

        cancelEventButton = (RelativeLayout)findViewById(R.id.cancel_event_button);
        cancelEventProgress = (RelativeLayout)findViewById(R.id.cancel_event_progress);
        canceledReasonEditText = (EditText)findViewById(R.id.edittext_canceled_reason);
    }

    public void cancelEvent(View view){
        cancelEventButton.setVisibility(View.GONE);
        cancelEventProgress.setVisibility(View.VISIBLE);

        ServiceFilterResponseCallback canceledEventResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    cancelEventButton.setVisibility(View.VISIBLE);
                    cancelEventProgress.setVisibility(View.GONE);
                    exception.printStackTrace();
                    return;
                }
                Toast.makeText(CancelEventActivity.this, R.string.canceled_event_toast, Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        String canceledReason = canceledReasonEditText.getText().toString();
        EventUtilities.cancelEvent(activity, eventId, canceledReason, mClient, canceledEventResponseCallback);
    }
}
