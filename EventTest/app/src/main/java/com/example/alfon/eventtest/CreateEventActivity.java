package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements CreateDescriptionFragment.OnFragmentInteractionListener {

    String DESCRIPTION_FRAGMENT_TAG = "Description fragment";

    Activity activity;
    MobileServiceClient mClient;

    Event eventToCreate;
    Calendar nowCalendar;
    Event createdEvent;

    List<User> selectedHosts;

    RelativeLayout createEventButton;
    RelativeLayout creatingEventProgressbar;

    CreateDescriptionFragment descriptionFragment;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventToCreate = new Event();
        nowCalendar = Calendar.getInstance();
        selectedHosts = new ArrayList<>();

        mClient = ((GlobalApplication) getApplication()).getmClient();
        activity = this;

        createEventButton = (RelativeLayout) findViewById(R.id.create_event_button);
        creatingEventProgressbar = (RelativeLayout) findViewById(R.id.creating_event_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ACTIVITY RESUMED!!!!!");
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void selectStartDate(View view) {
        final Calendar calendarToSet = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarToSet.set(Calendar.MINUTE, minute);

                eventToCreate.startTime = calendarToSet;
                ((TextView) findViewById(R.id.event_begins_value)).setText(
                        DateUtilities.dateToStringFormat(eventToCreate.startTime, "dd MMM (EEE) HH:mm"));
            }
        },
                nowCalendar.get(Calendar.HOUR_OF_DAY),
                nowCalendar.get(Calendar.MINUTE),
                true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarToSet.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                timePickerDialog.show();
            }
        },
                nowCalendar.get(Calendar.YEAR),
                nowCalendar.get(Calendar.MONTH),
                nowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void selectEndDate(View view) {
        final Calendar calendarToSet = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarToSet.set(Calendar.MINUTE, minute);

                eventToCreate.endTime = calendarToSet;
                ((TextView) findViewById(R.id.event_ends_value)).setText(
                        DateUtilities.dateToStringFormat(eventToCreate.endTime, "dd MMM (EEE) HH:mm"));
            }
        },
                nowCalendar.get(Calendar.HOUR_OF_DAY),
                nowCalendar.get(Calendar.MINUTE),
                true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarToSet.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                timePickerDialog.show();
            }
        },
                nowCalendar.get(Calendar.YEAR),
                nowCalendar.get(Calendar.MONTH),
                nowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void selectPlace(View view) {
        Intent selectPlaceIntent = new Intent(this, SelectLocationActivity.class);
        selectPlaceIntent.putExtra("location", (Serializable) eventToCreate.location);
        startActivityForResult(selectPlaceIntent, 0);
    }

    public void selectHosts(View view) {
        Intent selectHostsIntent = new Intent(this, SelectUsersActivity.class);
        selectHostsIntent.putExtra("users", (Serializable) selectedHosts);
        startActivityForResult(selectHostsIntent, 1);
    }

    public void selectCategories(View view) {
        Intent selectCategoriesIntent = new Intent(this, SelectCategoriesActivity.class);
        selectCategoriesIntent.putExtra("categories", (Serializable) eventToCreate.categories);
        startActivityForResult(selectCategoriesIntent, 2);
    }

    public void addDescription(View view){
        descriptionFragment = CreateDescriptionFragment.newInstance(((TextView) findViewById(R.id.event_description_value)).getText().toString());
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .add(R.id.description_fragment_container, descriptionFragment, DESCRIPTION_FRAGMENT_TAG)
                .addToBackStack("")
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * requestCode == 0 => selected location returned
         * requestCode == 1 => selected hosts returned
         * requestCode == 2 => selected categories returned
         */
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    System.out.println(new Gson().toJson(data.getSerializableExtra("selectedLocation")));
                    eventToCreate.location = (Location) data.getSerializableExtra("selectedLocation");
                    eventToCreate.locationId = eventToCreate.location.id;
                    ((TextView) findViewById(R.id.event_location_value)).setText(eventToCreate.location.shortName);
                    break;
                case 1:
                    selectedHosts = (List<User>) data.getSerializableExtra("users");
                    eventToCreate.hostIds = new ArrayList<>();
                    for (User u : selectedHosts) {
                        eventToCreate.hostIds.add(u.id);
                    }
                    ((TextView) findViewById(R.id.event_hosts_value)).setText(String.valueOf(selectedHosts.size()));
                    Toast.makeText(activity, new Gson().toJson(selectedHosts), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    eventToCreate.categories = (List<Category>) data.getSerializableExtra("categories");
                    eventToCreate.categoryIds = new ArrayList<>();
                    for (Category c : eventToCreate.categories) {
                        eventToCreate.categoryIds.add(c.id);
                    }
                    ((TextView) findViewById(R.id.event_categories_value)).setText(String.valueOf(eventToCreate.categories.size()));
                    Toast.makeText(activity, new Gson().toJson(eventToCreate.categories), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void createEvent(View view) {
        /**
         * TODO Felhantering för tomma fält osv.
         */
        final ServiceFilterResponseCallback hostsAddedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    Toast.makeText(activity, "Något gick fel", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, EventDetailsActivity.class);
                    intent.putExtra("event", createdEvent);
                    startActivity(intent);
                    finish();
                    return;
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                createdEvent = gson.fromJson(response.getContent(), Event.class);

                System.out.println(response.getContent());
                Intent intent = new Intent(activity, EventDetailsActivity.class);
                intent.putExtra("event", createdEvent);
                startActivity(intent);
                finish();
            }
        };
        ServiceFilterResponseCallback eventCreatedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    Toast.makeText(activity, "Något gick fel", Toast.LENGTH_SHORT).show();
                    createEventButton.setVisibility(View.VISIBLE);
                    creatingEventProgressbar.setVisibility(View.GONE);
                    return;
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                        .create();
                createdEvent = gson.fromJson(response.getContent(), Event.class);

                System.out.println(response.getContent());

                /**
                 * If you have selected hosts, post them as well
                 * Otherwise, go directly to EventDetailsActivity
                 */
                //if (!selectedHosts.isEmpty()) {
//
                //    String eventId = createdEvent.id;
//
                //    List<String> userIds = new ArrayList<>();
                //    for (User user : selectedHosts) {
                //        userIds.add(user.id);
                //    }
//
                //    //new EventUtilities().addHostsToEvent(activity, eventId, userIds, mClient, hostsAddedResponseCallback);
//
                //} else {
                Intent intent = new Intent(activity, EventDetailsActivity.class);
                intent.putExtra("event", createdEvent);
                startActivity(intent);
                finish();
                //}
            }
        };

        eventToCreate.name = ((EditText) findViewById(R.id.edittext_event_name)).getText().toString();
        eventToCreate.isPrivate = String.valueOf(((Switch) findViewById(R.id.event_is_private_value)).isChecked());

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                .create();

        System.out.println(gson.toJson(eventToCreate));
        new EventUtilities().createEvent(activity, gson.toJson(eventToCreate), mClient, eventCreatedResponseCallback);
        createEventButton.setVisibility(View.GONE);
        creatingEventProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishedAddingDescription(String description) {
        ((TextView) findViewById(R.id.event_description_value)).setText(description);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(DESCRIPTION_FRAGMENT_TAG));
        fragmentTransaction.commit();

        eventToCreate.description = description;

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
