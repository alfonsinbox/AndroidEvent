package com.example.alfon.eventtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

    public static void deleteFile(Activity activity, Uri fileUri){
        activity.getContentResolver().delete(fileUri, null, null);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        //bm.recycle();
        return resizedBitmap;
    }

    /**
     * Rotate a bitmap based on orientation metadata.
     * src - image path
     */
    public static Bitmap rotateBitmap(String fileSrc) {
        Bitmap bitmap = BitmapFactory.decodeFile(fileSrc);
        try {
            ExifInterface exif = new ExifInterface(fileSrc);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
