package com.example.alfon.eventtest;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * Created by alfon on 2016-06-29.
 */
public class FileUtilities {
    public static File createTemporaryFile(Activity activity, String fileName) {
        File tempDir = activity.getExternalFilesDir("");
        //System.out.println("I got an abs path: " + tempDir.getAbsolutePath());
        //if (!tempDir.exists()) {
        //    tempDir.mkdirs();
        //}
        return new File(tempDir, fileName);
    }

    public static File getImage(Uri imageUri) {
        try {
            return new File(imageUri.getPath());
        } catch (Exception e) {
            //Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
            System.out.println("Failed to load image from storage!");
        }
        return null;
    }

}
