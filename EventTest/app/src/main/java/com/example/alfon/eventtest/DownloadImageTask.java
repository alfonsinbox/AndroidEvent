package com.example.alfon.eventtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by alfon on 2016-06-30.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    AsyncTaskCallback failureAsyncTaskCallback;

    public DownloadImageTask(ImageView imageView, AsyncTaskCallback failureAsyncTaskCallback) {
        this.imageView = imageView;
        this.failureAsyncTaskCallback = failureAsyncTaskCallback;
    }

    protected Bitmap doInBackground(String... urls) {
        try {
            String url = urls[0];
            System.out.println("Loading image from URL: " + url);
            InputStream in = new java.net.URL(url).openStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            return mIcon;
        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
            e.printStackTrace();
            failureAsyncTaskCallback.asyncTaskCallbackDone(false);
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if(result != null) {
            imageView.setImageBitmap(result);
        }
    }
}
