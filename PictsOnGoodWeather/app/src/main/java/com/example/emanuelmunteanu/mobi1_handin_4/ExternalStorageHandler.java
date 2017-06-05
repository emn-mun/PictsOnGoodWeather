package com.example.emanuelmunteanu.mobi1_handin_4;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emanuelmunteanu on 25/09/15.
 */
public class ExternalStorageHandler {
    private Context context;

    public ExternalStorageHandler(Context context) {
        this.context = context;
    }

    // Check if External Storage is available for Read and Write
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Check if External Storage is available at least for Read
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void checkExternalStorage() {
        if (isExternalStorageWritable()) {
            Toast.makeText(context, "Can Read and Write to External Storage", Toast.LENGTH_SHORT).show();
        } else if (isExternalStorageReadable()) {
            Toast.makeText(context, "Can ONLY Read External Storage", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "CANNOT Read or Write to External Storage", Toast.LENGTH_SHORT).show();
        }
    }

    // Get directory for Saving Images. Create if not Exists
    // Create a folder inside DIRECTORY_PICTURES
    public File getPictureDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public File createImageFile() throws IOException {
        // Create a Unique image file name
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        String imagePath = "file:" + image.getAbsolutePath();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(MainActivity.PREV_IMAGE_PATH, imagePath).commit();

        return image;
    }
}
