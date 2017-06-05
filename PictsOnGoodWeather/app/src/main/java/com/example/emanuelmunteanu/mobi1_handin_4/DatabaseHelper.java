package com.example.emanuelmunteanu.mobi1_handin_4;

import com.activeandroid.query.Select;

public class DatabaseHelper {

    private static DatabaseHelper instance;

    public static DatabaseHelper get() {
        if (instance == null) instance = getSync();
        return instance;
    }

    private static synchronized DatabaseHelper getSync() {
        if (instance == null) instance = new DatabaseHelper();
        return instance;
    }

    public void addMarker(final MarkerModel markerModel) {
        markerModel.save();
    }

    public MarkerModel getMarker(final String id) {
        MarkerModel returnModel = new Select().from(MarkerModel.class)
                .where("markerId = ?", id)
                .executeSingle();

        if (returnModel == null)
            return null;

        return returnModel;
    }
}
