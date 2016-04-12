package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Locale;

public class SearchLocationActivity extends AppCompatActivity {

    Activity activity;

    AuthUtilities authUtilities;
    MobileServiceClient mClient;
    ListenableFuture<ServiceFilterResponse> locationSuggestionsListenableFuture;

    Location selectedLocation;

    EditText editTextSearchBox;
    ListView listviewExistingLocationSuggestions;
    ListView listviewGoogleLocationSuggestions;
    LinearLayout googleLocationsContainer;
    LinearLayout existingLocationsContainer;

    // To retrieve user location
    android.location.Location userLocation;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);


        activity = this;

        authUtilities = new AuthUtilities();
        mClient = ((GlobalApplication) this.getApplication()).getmClient();

        editTextSearchBox = (EditText) findViewById(R.id.edittext_search_locations);
        listviewExistingLocationSuggestions = (ListView) findViewById(R.id.listview_existing_location_suggestions);
        listviewGoogleLocationSuggestions = (ListView) findViewById(R.id.listview_google_location_suggestions);
        googleLocationsContainer = (LinearLayout)findViewById(R.id.google_locations_container);
        existingLocationsContainer = (LinearLayout)findViewById(R.id.existing_locations_container);


        try {
            userLocation = LocationUtilities.getUserLocation(activity);
        } catch (Exception e) {
            userLocation = new android.location.Location("dummyprovider");
            userLocation.setLatitude(0);
            userLocation.setLongitude(0);
        }

        latitude = String.valueOf(userLocation.getLatitude());
        longitude = String.valueOf(userLocation.getLongitude());

        editTextSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * TODO Move to runnable (?) and control timed requests so Google API won't be spammed with requests
                 */

                if(locationSuggestionsListenableFuture != null){
                    locationSuggestionsListenableFuture.cancel(true);
                }

                locationSuggestionsListenableFuture = LocationUtilities.getLocationSuggestionsFuture(activity,
                        s.toString(), latitude, longitude, mClient);

                Futures.addCallback(locationSuggestionsListenableFuture, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        populateListsFromResponse(result.getContent());
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });

        //Initial request, doesn't require a search query
        locationSuggestionsListenableFuture = LocationUtilities.getLocationSuggestionsFuture(activity,
                latitude, longitude, mClient);

        Futures.addCallback(locationSuggestionsListenableFuture, new FutureCallback<ServiceFilterResponse>() {
            @Override
            public void onSuccess(ServiceFilterResponse result) {
                populateListsFromResponse(result.getContent());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void returnSelectedLocation() {
        if (selectedLocation == null) {
            return;
        }
        Intent result = new Intent();
        result.putExtra("location", selectedLocation);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    public void navigateCreateLocation(View view) {
        Intent intent = new Intent(activity, CreateLocationActivity.class);
        startActivity(intent);
    }

    private void populateListsFromResponse(String response) {
        System.out.println(response);
        LocationSuggestionsResponse locationPredictions = new Gson().fromJson(response, LocationSuggestionsResponse.class);

        ExistingLocationPredictionsAdapter existingLocationPredictionsAdapter = new ExistingLocationPredictionsAdapter(activity, R.layout.list_item_existing_location_prediction, locationPredictions.existingLocations);
        GoogleLocationPredictionsAdapter googleLocationPredictionsAdapter = new GoogleLocationPredictionsAdapter(activity, R.layout.list_item_existing_location_prediction, locationPredictions.googleLocations);

        listviewExistingLocationSuggestions.setAdapter(existingLocationPredictionsAdapter);
        listviewGoogleLocationSuggestions.setAdapter(googleLocationPredictionsAdapter);

        ListUtilities.setDynamicHeight(listviewExistingLocationSuggestions);
        ListUtilities.setDynamicHeight(listviewGoogleLocationSuggestions);

        listviewExistingLocationSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = (Location) parent.getItemAtPosition(position);
                System.out.println(new Gson().toJson(selectedLocation));
                returnSelectedLocation();
            }
        });

        listviewGoogleLocationSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = (Location) parent.getItemAtPosition(position);
                System.out.println(new Gson().toJson(selectedLocation));
                getAndReturnGoogleDetails(selectedLocation.id);
            }
        });

        googleLocationsContainer.setVisibility(View.VISIBLE);
        existingLocationsContainer.setVisibility(View.VISIBLE);

        if(locationPredictions.googleLocations.isEmpty()){
            googleLocationsContainer.setVisibility(View.GONE);
        }
        if(locationPredictions.existingLocations.isEmpty()){
            existingLocationsContainer.setVisibility(View.GONE);
        }
    }

    private void getAndReturnGoogleDetails(String placeId){
        ServiceFilterResponseCallback locationRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                selectedLocation = new Gson().fromJson(response.getContent(), Location.class);
                returnSelectedLocation();
            }
        };
        LocationUtilities.getLocationDetails(activity, placeId, mClient, locationRetrievedResponseCallback);
    }
}
