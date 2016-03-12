package com.eugene.lysak.poiapplication.Utils;

import com.eugene.lysak.poiapplication.Models.POI;
import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by zeka on 11.03.16.
 */
public class SortPOI implements Comparator<POI> {

    LatLng currentLoc;

    public SortPOI(LatLng current){
        currentLoc = current;
    }

    @Override
    public int compare(POI lhs, POI rhs) {
        double lat1 = lhs.getLatitude();
        double lon1 = lhs.getLongitude();
        double lat2 = rhs.getLatitude();
        double lon2 = rhs.getLongitude();

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}
