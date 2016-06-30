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
    UrlImageView urlImage;

    public DownloadImageTask(UrlImageView urlImage) {
        this.urlImage = urlImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        try {
            System.out.println("Loading image from URL: " + url);
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        urlImage.setImageBitmap(result);
    }
}
