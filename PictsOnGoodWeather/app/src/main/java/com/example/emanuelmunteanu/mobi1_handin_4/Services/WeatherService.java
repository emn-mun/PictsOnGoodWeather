package com.example.emanuelmunteanu.mobi1_handin_4.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.emanuelmunteanu.mobi1_handin_4.Common.Constants;
import com.example.emanuelmunteanu.mobi1_handin_4.JSONWeatherParser;
import com.example.emanuelmunteanu.mobi1_handin_4.Model.Weather;
import com.example.emanuelmunteanu.mobi1_handin_4.WeatherHttpClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

/**
 * Created by emanuelmunteanu on 06/10/15.
 */
public class WeatherService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");

        if (lat != 0 && lng != 0) {
            String url = Constants.buildURLWithCoords(new LatLng(lat, lng));
            new GetWeather().execute(url);
        } else {
            new GetWeather().execute(Constants.BASE_URL);
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    private class GetWeather extends AsyncTask<String, String, Weather> {
        @Override
        protected Weather doInBackground(String... url) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(url[0]));
            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            Intent intent = new Intent(Constants.WEATHER_INTENT);
            intent.putExtra("city", weather.location.getCity());
            intent.putExtra("country", weather.location.getCountry());
            intent.putExtra("temperature", weather.temperature.getTemp());
            intent.putExtra("condition", weather.currentCondition.getCondition());
            intent.putExtra("description", weather.currentCondition.getDescr());
            intent.putExtra("humidity", weather.currentCondition.getHumidity());
            intent.putExtra("pressure", weather.currentCondition.getPressure());
            intent.putExtra("windSpeed", weather.wind.getSpeed());
            sendBroadcast(intent);
        }
    }
}
