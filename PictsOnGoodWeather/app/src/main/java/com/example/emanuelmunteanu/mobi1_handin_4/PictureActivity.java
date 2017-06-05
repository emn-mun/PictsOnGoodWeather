package com.example.emanuelmunteanu.mobi1_handin_4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;

public class PictureActivity extends AppCompatActivity {

    private ImageView mainPic;
    private EditText textArea;
    private Button saveButton;
    private String pictureTitle;
    private TextView markerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mainPic = (ImageView) findViewById(R.id.cameraPic);
        textArea = (EditText) findViewById(R.id.textArea);
        saveButton = (Button) findViewById(R.id.saveButton);
        markerText = (TextView) findViewById(R.id.markerText);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("pictureTitle")) {
            pictureTitle = bundle.getString("pictureTitle");
            findPicture(pictureTitle);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textArea.getText().toString().length() > 0) {
                    MarkerModel markerModel = DatabaseHelper.get().getMarker(pictureTitle);
                    markerModel.setMarkerText(textArea.getText().toString());
                    markerText.setText(textArea.getText().toString());
                    DatabaseHelper.get().addMarker(markerModel);
                    Intent intent = new Intent(PictureActivity.this, MapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please input something", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void findPicture(final String pictureTitle) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (pictureDirectory.exists()) {
                File[] files = pictureDirectory.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.getName().contains(pictureTitle)) {
                            setPictureToImageView(file.getPath(), mainPic);
                            checkMarkerText(textArea, file.getName());
                        }
                    }
                }
            }
        }
    }

    private void setPictureToImageView(String fileName, ImageView iv) {
        // Dimensions for View
        int targetH = 500;
        int targetW = 500;

        // Dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // How much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode image into a Bitmap
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmOptions);

        // Rotate according to EXIF
        try {
            ExifInterface exif = new ExifInterface(fileName);
            int or = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            iv.setRotation(or);
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv.setImageBitmap(bitmap);
    }

    private void checkMarkerText(final EditText editText, final String title) {

        MarkerModel markerModel = DatabaseHelper.get().getMarker(title);

        if (markerModel != null) {
            markerText.setText(markerModel.getMarkerText());
        }
    }

}
