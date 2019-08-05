package com.iskra.googlemapstutorial;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Angelika Iskra on 03.04.2018.
 */

public class Event {

    private Long dateEvent;
    private String id;
    private String type;
    private LatLng coordinates;

    public Event(Long dateEvent, String id, String type, LatLng coordinates) {
        this.dateEvent = dateEvent;
        this.id = id;
        this.type = type;
        this.coordinates = coordinates;
    }

    public Long getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(Long dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
}
