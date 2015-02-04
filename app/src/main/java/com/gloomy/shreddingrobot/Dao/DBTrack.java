package com.gloomy.shreddingrobot.Dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity mapped to table DBTRACK.
 */
public class DBTrack implements Parcelable {

    private Long id;
    private Double maxSpeed;
    private Double avgSpeed;
    private Double maxAirTime;
    private String locationName;
    private java.util.Date date;

    public DBTrack(Long id) {
        this.id = id;
        this.maxSpeed = 0.0;
        this.avgSpeed = 0.0;
        this.maxAirTime = 0.0;
        this.locationName = "";
        this.date = null;
    }

    public DBTrack(Long id, Double maxSpeed, Double avgSpeed,
                   Double maxAirTime, String locationName, java.util.Date date) {
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.maxAirTime = maxAirTime;
        this.locationName = locationName;
        this.date = date;
    }

    public DBTrack(Parcel parcel) {
        id = parcel.readLong();
        maxSpeed = parcel.readDouble();
        avgSpeed = parcel.readDouble();
        maxAirTime = parcel.readDouble();
        locationName = parcel.readString();
        date = new java.util.Date(parcel.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeLong(id);
        dest.writeDouble(maxSpeed);
        dest.writeDouble(avgSpeed);
        dest.writeDouble(maxAirTime);
        dest.writeString(locationName);
        dest.writeLong(date.getTime());
    }

    public static final Creator CREATOR
            = new Creator() {
        public DBTrack createFromParcel(Parcel in) {
            return new DBTrack(in);
        }

        public DBTrack[] newArray(int size) {
            return new DBTrack[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getMaxAirTime() {
        return maxAirTime;
    }

    public void setMaxAirTime(Double maxAirTime) {
        this.maxAirTime = maxAirTime;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }
}
