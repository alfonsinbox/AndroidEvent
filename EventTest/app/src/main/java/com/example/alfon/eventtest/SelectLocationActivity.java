package com.example.alfon.eventtest;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback, CustomLocationProvider.LocationCallback {

    Activity activity;

    AuthUtilities authUtilities;
    LocationUtilities locationUtilities;
    MobileServiceClient mClient;

    Location selectedLocation;
    android.location.Location userLocation;
    CustomLocationProvider mLocationProvider;

    TextView textViewSearchBox;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        activity = this;

        authUtilities = new AuthUtilities();
        locationUtilities = new LocationUtilities();
        mClient = GlobalApplication.getmClient();

        textViewSearchBox = (TextView) findViewById(R.id.textview_search_locations);

        selectedLocation = (Location) getIntent().getSerializableExtra("location");
        if (selectedLocation != null) {
            textViewSearchBox.setText(selectedLocation.longName);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationProvider = new CustomLocationProvider(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        if (selectedLocation == null) {

            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GlobalApplication.PERMISSIONS_REQUEST_FINE_LOCATION);
                return;
            }

            try {
                userLocation = LocationUtilities.getUserLocation(activity);
            } catch (Exception e) {
                e.printStackTrace();
                userLocation = new android.location.Location("dummyprovider");
                userLocation.setLatitude(0);
                userLocation.setLongitude(0);
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return;
        }

        // Create marker on map
        mMap.clear();

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(selectedLocation.latitude, selectedLocation.longitude)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(selectedLocation.latitude, selectedLocation.longitude))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            selectedLocation = (Location) data.getSerializableExtra("location");

            Toast.makeText(activity, new Gson().toJson(selectedLocation), Toast.LENGTH_SHORT).show();

            ((TextView) findViewById(R.id.textview_search_locations)).setText(selectedLocation.longName);

            // Create marker on map
            mMap.clear();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(selectedLocation.latitude, selectedLocation.longitude)));

            // Show selected location on Map
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(selectedLocation.latitude, selectedLocation.longitude))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void navigateLocationSearch(View view) {
        Intent intent = new Intent(this, SearchLocationActivity.class);
        startActivityForResult(intent, 0);
    }

    public void returnSelectedLocation(View view) {
        if (selectedLocation == null) {
            return;
        }
        Intent result = new Intent();
        result.putExtra("selectedLocation", selectedLocation);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GlobalApplication.PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            }
        }
    }

    @Override
    public void handleNewLocation(android.location.Location location) {
        userLocation = location;
        Log.d("Location", location.toString());
        System.out.println(location.toString());

        // Show selected location on Map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))      // Sets the center of the map to userLocation
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
