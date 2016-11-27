package helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by alfon on 2016-06-30.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    //ImageView imageView;
    IDownloadImageCallback mCallback;

    public DownloadImageTask(/*ImageView imageView,*/ IDownloadImageCallback callback) {
        //this.imageView = imageView;
        this.mCallback = callback;
    }

    protected Bitmap doInBackground(String... urls) {
        try {
            String url = urls[0];
            System.out.println("Loading image from URL: " + url);
            InputStream in = new java.net.URL(url).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            mCallback.onFailure(e);
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            //imageView.setImageBitmap(result);
            mCallback.onSuccess(result);
        }
    }
}
