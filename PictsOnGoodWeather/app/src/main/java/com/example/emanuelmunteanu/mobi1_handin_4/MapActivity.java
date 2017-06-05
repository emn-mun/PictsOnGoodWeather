package com.example.emanuelmunteanu.mobi1_handin_4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.emanuelmunteanu.mobi1_handin_4.Common.Constants;
import com.example.emanuelmunteanu.mobi1_handin_4.Services.WeatherService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private static final float INITIAL_ZOOM_LEVEL = 12.0f;
    private Button cameraButton;
    private LatLng latLng = new LatLng(55.8546437, 9.838756);

    private TextView weatherInfo1;
    private TextView weatherInfo2;
    private TextView weatherInfo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ActiveAndroid.initialize(this);

        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        buildGoogleApiClient();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        weatherInfo1 = (TextView) findViewById(R.id.map_weather_label);
        weatherInfo2 = (TextView) findViewById(R.id.map_weather_temp_desc);
        weatherInfo3 = (TextView) findViewById(R.id.map_weather_wind_press);
    }


    // Weather Service
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                weatherInfo1.setText("Weather for " + bundle.getString("city") + ", "
                        + bundle.getString("country") + " - " + bundle.getFloat("temperature"));
                weatherInfo2.setText(bundle.getString("condition") + ", " + bundle.getString("description"));
                weatherInfo3.setText("Humidity: " + bundle.getFloat("humidity") + ", Wind Speed: "
                        + bundle.getFloat("windSpeed"));
            }
        }
    };

    private void startWeatherService(LatLng latLng) {
        Intent weatherIntent = new Intent(MapActivity.this, WeatherService.class);
        weatherIntent.putExtra("lat", latLng.latitude);
        weatherIntent.putExtra("lng", latLng.longitude);
        startService(weatherIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        mMap = googleMap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, "Image name: " + marker.getTitle(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MapActivity.this, PictureActivity.class);
        intent.putExtra("pictureTitle", marker.getTitle());
        startActivity(intent);

        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), INITIAL_ZOOM_LEVEL));
            findPictures();

            LatLng coords = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            // for service
            startWeatherService(coords);

            // for widget
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.buildURLWithCoords(coords), Constants.WEATHER_PREF);

        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), INITIAL_ZOOM_LEVEL));
        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case 1: {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.google_play_disconnected), Toast.LENGTH_SHORT).show();
                break;
            }
            case 2: {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.connection_google_lost), Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                Toast.makeText(getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), INITIAL_ZOOM_LEVEL));
        }

        registerReceiver(receiver, new IntentFilter(Constants.WEATHER_INTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();

        unregisterReceiver(receiver);
    }

    private void findPictures() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (pictureDirectory.exists()) {
                File[] files = pictureDirectory.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.getName().endsWith(".jpg")) {
                            LatLng position = getPosition(file.getAbsolutePath());
                            if (position != null) {
                                addMarker(position, file.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private LatLng getPosition(final String filename) {
        float latLng[] = new float[2];
        LatLng pos = null;
        try {
            ExifInterface exifInterface = new ExifInterface(filename);
            if (exifInterface.getLatLong(latLng)) {
                pos = new LatLng(latLng[0], latLng[1]);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MapActivity.this, "Picture location could not be retrieved", Toast.LENGTH_SHORT).show();
        }
        return pos;
    }

    private void addMarker(final LatLng position, final String filename) {
        GoogleMap map = getmMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.addMarker(new MarkerOptions()
                    .position(position).title(filename));
        }

        MarkerModel markerModel = new MarkerModel();
        markerModel.setMarkerId(filename);

        DatabaseHelper.get().addMarker(markerModel);
    }
}
