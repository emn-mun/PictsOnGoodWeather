package com.example.emanuelmunteanu.mobi1_handin_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.emanuelmunteanu.mobi1_handin_4.Common.Constants;
import com.example.emanuelmunteanu.mobi1_handin_4.Model.Weather;
import org.json.JSONException;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    public static String WIDGET_BUTTON = "ChecKSite";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        // Register Click Listener on Widget --> Might Cause Refresh
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        ComponentName widget = new ComponentName(context, AppWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.temperature_text_view, getPendingSelfIntent(context, WIDGET_BUTTON));
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        new AsyncTask<String, Void, Weather>() {
            @Override
            protected Weather doInBackground(String... params) {
                Weather weather = new Weather();

                String lastLocationURL = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(Constants.WEATHER_PREF, Constants.BASE_URL);
                String data = ( (new WeatherHttpClient()).getWeatherData(lastLocationURL));

                try {
                    weather = JSONWeatherParser.getWeather(data);

                    // Let's retrieve the icon
                    weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return weather;
            }

            @Override
            protected void onPostExecute(Weather weather) {
                super.onPostExecute(weather);

                if (weather.iconData != null && weather.iconData.length > 0) {
                    Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                    views.setImageViewBitmap(R.id.weather_icon_image_view, img);
                }

                views.setTextViewText(R.id.temperature_text_view, "" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
                views.setTextViewText(R.id.weather_condition_text_view, weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescr() + ")");
                views.setTextViewText(R.id.city_text_view, weather.location.getCity());
                views.setTextViewText(R.id.country_text_view, weather.location.getCountry());
                views.setTextViewText(R.id.pressure_text_view, "" + weather.currentCondition.getHumidity() + "%");
                views.setTextViewText(R.id.humidity_text_view, "" + weather.currentCondition.getPressure() + " hPa");
                views.setTextViewText(R.id.wind_text_view, "" + weather.wind.getSpeed() + " mps");

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }.execute();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            Log.v("Refresh", "bloody weather !!!");
        }
    }
}
