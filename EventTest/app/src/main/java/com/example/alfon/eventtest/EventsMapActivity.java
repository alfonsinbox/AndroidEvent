package com.example.alfon.eventtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsMapActivity extends AppCompatActivity implements OnMapReadyCallback, CustomLocationProvider.LocationCallback {

    /**
     * HashMap used to keep track on the
     * references between every event
     * marker on the map.
     */
    HashMap<Marker, Event> hashMap = new HashMap<>();

    Activity activity;

    AuthUtilities authUtilities;
    MobileServiceClient mClient;
    android.location.Location userLocation;
    GoogleMap mMap;
    private CustomLocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        activity = this;

        authUtilities = new AuthUtilities();
        mClient = GlobalApplication.getmClient();

        mLocationProvider = new CustomLocationProvider(this, this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    /**
     * Called when map is finished loading
     *
     * @param map
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        ServiceFilterResponseCallback serviceFilterResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                System.out.println("THIS WENT WELL! " + response.getContent());
                Gson gson = new GsonBuilder().registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer()).create();
                List<Event> events = gson.fromJson(response.getContent(), new TypeToken<List<Event>>() {
                }.getType());
                for (Event event : events) {
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(event.location.latitude, event.location.longitude))
                            .title(event.name));
                    hashMap.put(marker, event);
                }
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Event selectedEvent = hashMap.get(marker);
                        Intent intent = new Intent(activity, EventDetailsActivity.class);
                        intent.putExtra("event", selectedEvent);
                        startActivity(intent);
                        Toast.makeText(activity, hashMap.get(marker).location.shortName, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        };

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Globals.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        if (userLocation != null) {
            // TODO The value '8000' is search radius, change this when zooming out or moving (ask before refreshing?)
            EventUtilities.getEventsForMap(activity, String.valueOf(userLocation.getLatitude()),
                    String.valueOf(userLocation.getLongitude()), "8000", mClient, serviceFilterResponseCallback);
        }

        map.setMyLocationEnabled(true);
    }

    public void navigateEventSearch(View view) {
        Intent intent = new Intent(activity, SearchEventsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GlobalApplication.PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationProvider.connect();
                }
                break;
            }
        }
    }

    @Override
    public void handleNewLocation(Location location) {
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