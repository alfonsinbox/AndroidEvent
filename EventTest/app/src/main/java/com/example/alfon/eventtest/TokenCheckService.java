package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by alfon on 2016-07-20.
 */
public class TokenCheckService extends Service {

    private boolean hasStarted = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SERVICE START COMMAND");
        if(!hasStarted){
            hasStarted = true;
            System.out.println("SERVICE HAS STARTED");
            TokenCheck tokenCheck = new TokenCheck(getApplicationContext(), GlobalApplication.getmClient()) {
                @Override
                public void onSuccess() {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
                    sendBroadcast(broadcastIntent);
                    System.out.println("TOKEN CHECK FINISHED WITH SUCCESS");
                    stopSelf();
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO: 2016-07-21 deal with failures
                    // Perhaps send back a broadcast with failure message..?
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_FAILURE);
                    sendBroadcast(broadcastIntent);
                    System.out.println("TOKEN CHECK FINISHED WITH FAILURE");
                    stopSelf();
                }
            };
            tokenCheck.checkToken();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("TOKEN CHECK FINISHED FOR REAL (SERVICE WAS DESTROYED)");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
