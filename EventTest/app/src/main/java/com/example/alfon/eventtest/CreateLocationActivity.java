package com.example.alfon.eventtest;

import android.app.Activity;
import android.location.*;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class CreateLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Activity activity;
    MobileServiceClient mClient;

    Location userLocation;

    LatLng selectedLatLng;
    String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        activity = this;
        mClient = ((GlobalApplication) this.getApplication()).getmClient();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        try {
            userLocation = LocationUtilities.getUserLocation(activity);
        } catch (Exception e) {
            userLocation = new android.location.Location("dummyprovider");
            userLocation.setLatitude(0);
            userLocation.setLongitude(0);
        }

        // Show selected location on Map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLatLng = latLng;
                System.out.println(latLng.latitude + " " + latLng.longitude);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude)));
                // Set new marker, ask for name and send to server
                // Maybe pick center on map instead and
                // just center the location you want to use
            }
        });
    }

    public void createLocation(View view){
        System.out.println("Checking all values");
        locationName = ((EditText)findViewById(R.id.edittext_location_name)).getText().toString();
        if(selectedLatLng != null && !locationName.equals("")){
            // Create location
            System.out.println("Will create location");

            ServiceFilterResponseCallback locationCreatedResponseCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    System.out.println("Has returned from request");
                    if(exception != null){
                        System.out.println("Threw exception");
                        exception.printStackTrace();
                        return;
                    }
                    System.out.println(response.getContent());
                    finish();
                }
            };

            new LocationUtilities().createLocation(activity, locationName, String.valueOf(selectedLatLng.latitude),
                    String.valueOf(selectedLatLng.longitude), mClient, locationCreatedResponseCallback);

        }
    }

}
