package com.eugene.lysak.poiapplication.Models;

import com.orm.SugarRecord;

/**
 * Created by zeka on 11.03.16.
 */
public class POI extends SugarRecord {

    private String name;
    private double longitude;
    private double latitude;
    private String photo;
    private String description;

    private boolean favorite;

    public POI(){
    }

    public POI(String name,String photo,String description,double longitude,double latitude,boolean favorite)
    {
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
