package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Activity activity;

    AuthUtilities authUtilities;
    LocationUtilities locationUtilities;
    MobileServiceClient mClient;

    Location selectedLocation;

    TextView textViewSearchBox;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        activity = this;

        authUtilities = new AuthUtilities();
        locationUtilities = new LocationUtilities();
        mClient = ((GlobalApplication) this.getApplication()).getmClient();

        textViewSearchBox = (TextView) findViewById(R.id.textview_search_locations);

        selectedLocation = (Location)getIntent().getSerializableExtra("location");
        if (selectedLocation != null) {
            textViewSearchBox.setText(selectedLocation.longName);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (selectedLocation == null){
            android.location.Location location = null;
            try {
                location = LocationUtilities.getUserLocation(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return;
        }
        // Create marker on map
        map.clear();
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(selectedLocation.latitude, selectedLocation.longitude)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(selectedLocation.latitude, selectedLocation.longitude))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            selectedLocation = (Location) data.getSerializableExtra("location");

            Toast.makeText(activity,new Gson().toJson(selectedLocation), Toast.LENGTH_SHORT).show();

            ((TextView)findViewById(R.id.textview_search_locations)).setText(selectedLocation.longName);

            // Create marker on map
            map.clear();
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(selectedLocation.latitude, selectedLocation.longitude)));

            // Show selected location on Map
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(selectedLocation.latitude, selectedLocation.longitude))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void navigateLocationSearch(View view){
        Intent intent = new Intent(this, SearchLocationActivity.class);
        startActivityForResult(intent, 0);
    }

    public void returnSelectedLocation(View view){
        if (selectedLocation == null) {
            return;
        }
        Intent result = new Intent();
        result.putExtra("selectedLocation", selectedLocation);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
