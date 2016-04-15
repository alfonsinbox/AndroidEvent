package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;
import com.microsoft.windowsazure.mobileservices.table.serialization.DateSerializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;

public class MainActivity extends AppCompatActivity  {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        System.out.println(activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, MODE_PRIVATE).getString(GlobalApplication.PREFERENCE_REGISTRATION_ID, ""));
    }

    public void navigateProfileOverview(View view){
        Intent intent = new Intent(activity, UserOverviewActivity.class);
        startActivity(intent);
    }
    public void navigateEventSearch(View view){
        Intent intent = new Intent(activity, SearchEventsActivity.class);
        startActivity(intent);
    }
    public void navigateEventCreate(View view){
        Intent intent = new Intent(activity, CreateEventActivity.class);
        startActivity(intent);
    }
    public void navigateEventMap(View view){
        Intent intent = new Intent(activity, EventsMapActivity.class);
        startActivity(intent);
    }
    public void signOut(View view){
        if(AuthUtilities.removeToken(activity)){
            Intent intent = new Intent(activity, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
