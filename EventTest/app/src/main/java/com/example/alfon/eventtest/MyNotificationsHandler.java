package com.example.alfon.eventtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

/**
 * Created by alfon on 2016-04-14.
 */
public class MyNotificationsHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onRegistered(Context context, final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                try {
                    GlobalApplication.mClient.getPush().register(gcmRegistrationId);
                    return null;
                }
                catch(Exception e) {
                    // handle error
                }
                return null;
            }
        }.execute();

    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        super.onReceive(context, bundle);
        String msg = bundle.getString("message");

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, // requestCode
                new Intent(context, UserOverviewActivity.class),
                0); // flags

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Hub Demo")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
