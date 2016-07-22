package com.example.alfon.eventtest;

import android.app.Application;
import android.content.Intent;

/**
 * Created by alfon on 2016-02-19.
 */
public class OnApplicationStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //if(!this.getSharedPreferences("UserSettings", 0).getString("token", "").equals("")) {
        //    Intent intent = new Intent(this, MainActivity.class);
        //    startActivity(intent);
        //}
    }
}
