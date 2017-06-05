package com.example.emanuelmunteanu.mobi1_handin_4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_IMAGE_CAPTURE = 1;
    public static String PREV_IMAGE_PATH;
    private ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler(this);

    private Button takePictureButton;
    private ImageView takenPictureImageView;

    // region Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = (Button) findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(this);
        takenPictureImageView = (ImageView) findViewById(R.id.taken_picture_image_view);

        String imagePath = PreferenceManager.getDefaultSharedPreferences(this).getString(PREV_IMAGE_PATH, "");
        if (!imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            takenPictureImageView.setImageURI(Uri.fromFile(imgFile));
        }

        externalStorageHandler.checkExternalStorage();
    }
    // endregion

    // region Camera Intent
    private void dispatchTakePictureIntent() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = picturesDirectory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PREV_IMAGE_PATH, imageFileName).commit();

        File imageFile = new File(imageFileName);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }
    // endregion

    // region Camera Intent Delegate
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String imagePath = PreferenceManager.getDefaultSharedPreferences(this).getString(PREV_IMAGE_PATH, "");
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_picture_button:
                dispatchTakePictureIntent();
                break;
        }
    }
    // endregion

}
