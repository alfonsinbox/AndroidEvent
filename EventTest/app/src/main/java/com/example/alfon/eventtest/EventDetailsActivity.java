package com.example.alfon.eventtest;

import android.app.Activity;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.alfon.eventtest.databinding.ActivityEventDetailsBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Calendar;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    Activity activity;
    MobileServiceClient mClient;

    Event passedEvent;
    ActivityEventDetailsBinding activityEventDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        mClient = ((GlobalApplication) getApplication()).getmClient();

        passedEvent = (Event) getIntent().getSerializableExtra("event");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                .create();
        System.out.println(gson.toJson(passedEvent));

        activityEventDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);
        activityEventDetailsBinding.setEvent(passedEvent);
    }

    public void addUserAsAttendant(View view) {
        ServiceFilterResponseCallback attendantAddedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    exception.printStackTrace();
                    return;
                }
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                passedEvent = gson.fromJson(response.getContent(), Event.class);
                activityEventDetailsBinding.setEvent(passedEvent);
                Toast.makeText(activity, "ATTEND SUCCESS", Toast.LENGTH_SHORT).show();
            }
        };
        new EventUtilities().addAttendantToEvent(activity, passedEvent.id, mClient, attendantAddedResponseCallback);
    }

    public void removeUserAsAttendant(View view) {
        ServiceFilterResponseCallback attendantAddedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    exception.printStackTrace();
                    return;
                }
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                passedEvent = gson.fromJson(response.getContent(), Event.class);
                activityEventDetailsBinding.setEvent(passedEvent);
                Toast.makeText(activity, "DONT ATTEND SUCCESS", Toast.LENGTH_SHORT).show();
            }
        };
        new EventUtilities().removeAttendantFromEvent(activity, passedEvent.id, mClient, attendantAddedResponseCallback);
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
            if(startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)){
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            }else if(startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)){
                return String.format("%s - %s",
                        DateUtilities.dateToStringFormat(startDate, "dd"),
                        DateUtilities.dateToStringFormat(endDate, "dd MMMM YYYY"));
            }else{
                return DateUtilities.dateToStringFormat(startDate, "dd MMMM YYYY");
            }
        } else if (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd MMMM"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else if (startDate.get(Calendar.DAY_OF_MONTH) != endDate.get(Calendar.DAY_OF_MONTH)){
            return String.format("%s - %s",
                    DateUtilities.dateToStringFormat(startDate, "dd"),
                    DateUtilities.dateToStringFormat(endDate, "dd MMMM"));
        } else {
            return DateUtilities.dateToStringFormat(startDate, "dd MMMM");
        }
    }

    public static String formatTimeStartToEnd(Calendar startDate, Calendar endDate){
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
        if(passedEvent.isCreator) {
            popup.getMenu().add(1, R.id.menu_item_edit_event, 1, R.string.menu_item_edit_event);
            popup.getMenu().add(1, R.id.menu_item_cancel_event, 1, R.string.menu_item_cancel_event);
            popup.getMenu().add(1, R.id.menu_item_delete_event, 1, R.string.menu_item_delete_event);
        }else {
            if (!passedEvent.isAttending) {
                popup.getMenu().add(1, R.id.menu_item_attend_event, 1, R.string.menu_item_attend_event);
            }else{
                popup.getMenu().add(1, R.id.menu_item_dont_attend_event, 1, R.string.menu_item_dont_attend_event);
            }
        }
        /*popup.inflate(R.menu.event_details_options);*/
        popup.show();
    }
    
    private void editEvent(){
        Toast.makeText(activity, "EDIT", Toast.LENGTH_SHORT).show();
    }
    private void deleteEvent(){
        Toast.makeText(activity, "DELETE", Toast.LENGTH_SHORT).show();
    }
    private void cancelEvent(){
        Toast.makeText(activity, "CANCEL", Toast.LENGTH_SHORT).show();
    }
    private void attendEvent(){
        addUserAsAttendant(null);
    }
    private void dontAttendEvent(){
        removeUserAsAttendant(null);
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
                cancelEvent();
                return true;
            case R.id.menu_item_attend_event:
                attendEvent();
                return true;
            case R.id.menu_item_dont_attend_event:
                dontAttendEvent();
                return true;
            default:
                return false;
        }
    }
}
