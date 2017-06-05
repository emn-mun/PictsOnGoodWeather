package com.example.emanuelmunteanu.mobi1_handin_4;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Pop Alex-Cristian on 10/5/2015.
 */

@Table(name = "markers")

public class MarkerModel extends Model {

    @Column
    private String markerId;
    @Column
    private String markerText;

    public MarkerModel() {
    }

    public String getMarkerText() {
        return markerText;
    }

    public void setMarkerText(String markerText) {
        this.markerText = markerText;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
