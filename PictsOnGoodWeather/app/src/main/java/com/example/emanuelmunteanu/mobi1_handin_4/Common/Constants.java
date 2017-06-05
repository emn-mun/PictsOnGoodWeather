package com.example.emanuelmunteanu.mobi1_handin_4.Common;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by emanuelmunteanu on 06/10/15.
 */
public class Constants {
    public final static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?lat=55.855973&lon=9.850771&APPID=cd3e312a04e4dd45addf39384d20a0f3";
    public final static String IMG_URL = "http://openweathermap.org/img/w/";
    public final static String WEATHER_INTENT = "WeatherIntent";
    public final static String WEATHER_PREF = "WeatherPref";

    public static String buildURLWithCoords(LatLng latLng) {
        return "http://api.openweathermap.org/data/2.5/weather?APPID=cd3e312a04e4dd45addf39384d20a0f3&lat=" + latLng.latitude + "&lon=" + latLng.longitude;
    }
}
