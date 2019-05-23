package com.example.gpstracker.pojo.updateLocationRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Fields {

    @SerializedName("alarm")
    @Expose
    private String alarm;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("track")
    @Expose
    private String track;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("lat")
    @Expose
    private Double lat;

    public Fields(Double lon, Double lat, String alarm) {
        this.alarm = alarm;
        this.lon = lon;
        this.lat = lat;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLon() {
        return lon;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTrack() {
        return track;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }
}
