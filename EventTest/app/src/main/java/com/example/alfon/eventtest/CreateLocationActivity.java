package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class CreateLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Activity activity;
    MobileServiceClient mClient;

    Location userLocation;

    LatLng selectedLatLng;
    String locationName;

    GoogleMap mMap;

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

        mMap = googleMap;

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLatLng = latLng;
                System.out.println(latLng.latitude + " " + latLng.longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude)));
                // Set new marker, ask for name and send to server
                // Maybe pick center on map instead and
                // just center the location you want to use
            }
        });

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Globals.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

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

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void createLocation(View view) {
        System.out.println("Checking all values");
        locationName = ((EditText) findViewById(R.id.edittext_location_name)).getText().toString();
        if (selectedLatLng != null && !locationName.equals("")) {
            // Create location
            System.out.println("Will create location");

            ServiceFilterResponseCallback locationCreatedResponseCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    System.out.println("Has returned from request");
                    if (exception != null) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Globals.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    break;
                }
            }

        }
    }
}
