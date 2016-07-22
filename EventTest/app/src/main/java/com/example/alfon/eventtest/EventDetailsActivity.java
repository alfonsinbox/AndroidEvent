package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alfon.eventtest.databinding.ActivityEventDetailsBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.File;
import java.util.Calendar;

public class EventDetailsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    Activity activity;
    MobileServiceClient mClient;

    Event detailedEvent;

    ActivityEventDetailsBinding activityEventDetailsBinding;

    ImageView eventImageView;
    Uri uploadImageUri;

    AsyncTaskCallback failedLoadingImageCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        mClient = GlobalApplication.getmClient();

        detailedEvent = GlobalApplication.currentlyDetailedEvent;

        failedLoadingImageCallback = new AsyncTaskCallback() {
            @Override
            public void asyncTaskCallbackDone(Boolean success) {
                if(!success){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventImageView.setImageResource(R.drawable.no_image_banner);
                        }
                    });
                }
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                .create();

        if (getIntent().getExtras().getBoolean(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA, false)) {
            ServiceFilterResponseCallback fetchedEventResponseCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                        return;
                    }
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                            .create();
                    System.out.println("RESULTS " + response.getContent());
                    detailedEvent = gson.fromJson(response.getContent(), Event.class);
                    activityEventDetailsBinding = DataBindingUtil.setContentView(activity, R.layout.activity_event_details);
                    activityEventDetailsBinding.setEvent(detailedEvent);

                    eventImageView = (ImageView) findViewById(R.id.event_details_image_view);
                    new DownloadImageTask(eventImageView, failedLoadingImageCallback).execute(detailedEvent.mainImageUrl);

                    System.out.println("WAS FETCHED - " + gson.toJson(detailedEvent));
                }
            };
            String eventId = getIntent().getExtras().getString(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID);
            EventUtilities.getEventDetails(activity, eventId, mClient, fetchedEventResponseCallback);

        } else {
            detailedEvent = (Event) getIntent().getSerializableExtra("event");
            activityEventDetailsBinding = DataBindingUtil.setContentView(activity, R.layout.activity_event_details);
            activityEventDetailsBinding.setEvent(detailedEvent);

            eventImageView = (ImageView) findViewById(R.id.event_details_image_view);
            new DownloadImageTask(eventImageView, failedLoadingImageCallback).execute(detailedEvent.mainImageUrl);

            System.out.println("WAS PASSED - " + gson.toJson(detailedEvent));
        }

    }

    private void refreshEvent() {
        ServiceFilterResponseCallback fetchedEventResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                System.out.println("RESULTS " + response.getContent());
                detailedEvent = gson.fromJson(response.getContent(), Event.class);
                activityEventDetailsBinding = DataBindingUtil.setContentView(activity, R.layout.activity_event_details);
                activityEventDetailsBinding.setEvent(detailedEvent);

                eventImageView = (ImageView) findViewById(R.id.event_details_image_view);
                new DownloadImageTask(eventImageView, failedLoadingImageCallback).execute(detailedEvent.mainImageUrl);

                System.out.println("WAS FETCHED - " + gson.toJson(detailedEvent));
            }
        };
        String eventId = detailedEvent.id;
        EventUtilities.getEventDetails(activity, eventId, mClient, fetchedEventResponseCallback);

    }

    public void addUserAsAttendant(View view) {
        ServiceFilterResponseCallback attendantAddedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                detailedEvent = gson.fromJson(response.getContent(), Event.class);
                activityEventDetailsBinding.setEvent(detailedEvent);
                Toast.makeText(activity, "ATTEND SUCCESS", Toast.LENGTH_SHORT).show();
            }
        };
        EventUtilities.addAttendantToEvent(activity, detailedEvent.id, mClient, attendantAddedResponseCallback);
    }

    public void removeUserAsAttendant(View view) {
        ServiceFilterResponseCallback attendantAddedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                detailedEvent = gson.fromJson(response.getContent(), Event.class);
                activityEventDetailsBinding.setEvent(detailedEvent);
                Toast.makeText(activity, "DONT ATTEND SUCCESS", Toast.LENGTH_SHORT).show();
            }
        };
        EventUtilities.removeAttendantFromEvent(activity, detailedEvent.id, mClient, attendantAddedResponseCallback);
    }

    public static String dateToStringConversion(Calendar calendar) {
        return DateUtilities.dateToStringFormat(calendar, "dd MMMM (EEEE) HH:mm");
    }

    public static String formatDateStartToEnd(Calendar startDate, Calendar endDate) {
        if (startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd MMMM YYYY"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
        } else if (startDate.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
            if (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            } else if (startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)) {
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            } else {
                return DateUtilities.dateToStringFormat(startDate, "dd MMMM YYYY");
            }
        } else if (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else if (startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else {
            return DateUtilities.dateToStringFormat(startDate, "dd MMMM");
        }
    }

    public static String formatTimeStartToEnd(Calendar startDate, Calendar endDate) {
        return String.format("%s - %s",
                DateUtilities.dateToStringFormat(startDate, "HH:mm"),
                DateUtilities.dateToStringFormat(endDate, "HH:mm"));
    }

    @BindingConversion
    public static String booleanToStringConversion(boolean b) {
        return b ? "Jag kommer" : "Jag kommer inte";
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(this);
        if (detailedEvent.isCreator) {
            popup.getMenu().add(1, R.id.menu_item_edit_event, 1, R.string.menu_item_edit_event);
            popup.getMenu().add(1, R.id.menu_item_cancel_event, 1, R.string.menu_item_cancel_event);
            popup.getMenu().add(1, R.id.menu_item_delete_event, 1, R.string.menu_item_delete_event);
        } else {
            if (!detailedEvent.isAttending) {
                popup.getMenu().add(1, R.id.menu_item_attend_event, 1, R.string.menu_item_attend_event);
            } else {
                popup.getMenu().add(1, R.id.menu_item_dont_attend_event, 1, R.string.menu_item_dont_attend_event);
            }
        }
        popup.getMenu().add(1, R.id.menu_item_navigate_to_event, 1, R.string.menu_item_navigate_to_event);
        popup.show();
    }

    private void editEvent() {
        Toast.makeText(activity, "EDIT", Toast.LENGTH_SHORT).show();
    }

    private void deleteEvent() {
        Toast.makeText(activity, "DELETE", Toast.LENGTH_SHORT).show();
    }

    private void navigateCancelEvent() {
        //Toast.makeText(activity, "CANCEL", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, CancelEventActivity.class);
        intent.putExtra(GlobalApplication.EXTRA_CANCELEVENT_EVENTID, detailedEvent.id);
        startActivity(intent);
    }

    private void attendEvent() {
        addUserAsAttendant(null);
    }

    private void dontAttendEvent() {
        removeUserAsAttendant(null);
    }

    private void startGoogleMapsNavigateIntent() {
        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s,%s", detailedEvent.location.latitude, detailedEvent.location.longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_edit_event:
                editEvent();
                return true;
            case R.id.menu_item_delete_event:
                deleteEvent();
                return true;
            case R.id.menu_item_cancel_event:
                navigateCancelEvent();
                return true;
            case R.id.menu_item_attend_event:
                attendEvent();
                return true;
            case R.id.menu_item_dont_attend_event:
                dontAttendEvent();
                return true;
            case R.id.menu_item_navigate_to_event:
                startGoogleMapsNavigateIntent();
                return true;
            default:
                return false;
        }
    }

    public void startCameraIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = FileUtilities.createTemporaryFile(activity, "IMG_" + System.currentTimeMillis() + ".jpg");
        uploadImageUri = Uri.fromFile(photo);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadImageUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE:
                    uploadImageToEvent();
                    break;
            }
        }
    }

    private void uploadImageToEvent() {
        EventUtilities.UploadEventImageTask uploadEventImageTask = new EventUtilities.UploadEventImageTask(activity, uploadImageUri, detailedEvent, new AsyncTaskCallback() {
            @Override
            public void asyncTaskCallbackDone(Boolean success) {
                refreshEvent();
            }
        });
        uploadEventImageTask.execute();
    }
}
