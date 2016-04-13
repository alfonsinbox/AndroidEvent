package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchEventsActivity extends AppCompatActivity {

    Activity activity;

    AuthUtilities authUtilities;
    EventUtilities eventUtilities;
    MobileServiceClient mClient;

    Event selectedEvent;

    EditText editTextSearchBox;
    ListView listViewEventSuggestions;
    ProgressBar searchingEventProgress;

    String searchString;
    ListenableFuture<ServiceFilterResponse> eventListenableFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);
        activity = this;

        authUtilities = new AuthUtilities();
        eventUtilities = new EventUtilities();
        mClient = ((GlobalApplication) this.getApplication()).getmClient();
        searchString = "";

        editTextSearchBox = (EditText) findViewById(R.id.edittext_search_events);
        listViewEventSuggestions = (ListView) findViewById(R.id.listview_event_suggestions);
        searchingEventProgress = (ProgressBar) findViewById(R.id.searching_events_progress);

        editTextSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchString = s.toString().trim();
                searchEvents(searchString);
            }
        });

    }

    @Override
    protected void onResume() {
        System.out.println("Activity was resumed");
        listViewEventSuggestions.setAdapter(null);
        searchEvents(searchString);
        super.onResume();
    }

    private void searchEvents(String query){
        listViewEventSuggestions.setAdapter(null);
        searchingEventProgress.setVisibility(View.VISIBLE);

        if(eventListenableFuture != null) {
            eventListenableFuture.cancel(true);
        }

        if(query.equals("")){
            listViewEventSuggestions.setAdapter(null);
            searchingEventProgress.setVisibility(View.GONE);
            return;
        }

        eventListenableFuture = EventUtilities.searchEventsFuture(activity, query, mClient);

        Futures.addCallback(eventListenableFuture, new FutureCallback<ServiceFilterResponse>() {
            @Override
            public void onSuccess(ServiceFilterResponse result) {
                searchingEventProgress.setVisibility(View.GONE);
                System.out.println("FUCKING SUCCESS!!!" + result.getContent());
                Gson gson = new GsonBuilder().registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer()).create();
                List<Event> eventSearchResults = gson.fromJson(result.getContent(), new TypeToken<List<Event>>() {
                }.getType());
                EventSearchAdapter eventSearchAdapter = new EventSearchAdapter(activity, R.layout.list_item_event_search, eventSearchResults);
                listViewEventSuggestions.setAdapter(eventSearchAdapter);
                listViewEventSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedEvent = (Event) parent.getItemAtPosition(position);
                        System.out.println(new Gson().toJson(selectedEvent));
                        Intent intent = new Intent(activity, EventDetailsActivity.class);
                        intent.putExtra("event", selectedEvent);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                searchingEventProgress.setVisibility(View.GONE);
                System.out.println("FUCKING FAILURE!!!");
            }
        });
    }
}

