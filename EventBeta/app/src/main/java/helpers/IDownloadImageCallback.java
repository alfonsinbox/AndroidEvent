package helpers;

import android.graphics.Bitmap;

/**
 * Created by alfonslange on 16/09/16.
 */
public interface IDownloadImageCallback {
    void onSuccess(Bitmap bitmap);
    void onFailure(Exception e);
}
