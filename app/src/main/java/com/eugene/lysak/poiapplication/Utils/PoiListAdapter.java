package com.eugene.lysak.poiapplication.Utils;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.eugene.lysak.poiapplication.Models.POI;
import com.eugene.lysak.poiapplication.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeka on 11.03.16.
 */
public class PoiListAdapter extends BaseAdapter implements Filterable{

    private Context mContext;
    private List<POI> data = null;
    private List<POI> orig = null;
    private LayoutInflater inflater;
    private Location currentLocation;

    public PoiListAdapter(Context mContext, List<POI> data,LatLng location) {
        this.mContext = mContext;
        this.data = data;
        this.inflater = LayoutInflater.from(mContext);
        currentLocation = new Location("currentLocation");
        currentLocation.setLongitude(location.longitude);
        currentLocation.setLatitude(location.latitude);
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public POI getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView picture;
        TextView name;
        TextView distance;

        if(v == null) {
            v = inflater.inflate(R.layout.list_item, parent, false);
            v.setTag(R.id.img_poi, v.findViewById(R.id.img_poi));
            v.setTag(R.id.name_poi, v.findViewById(R.id.name_poi));
            v.setTag(R.id.distance, v.findViewById(R.id.distance));
        }

        picture = (ImageView)v.getTag(R.id.img_poi);
        name = (TextView)v.getTag(R.id.name_poi);
        distance = (TextView)v.getTag(R.id.distance);

        Uri uri = null;
        if(data.get(position).getPhoto()!=null) {
            uri = Uri.fromFile(new File(data.get(position).getPhoto()));
            Picasso.with(mContext).load(uri)
                    .resize(96, 96).centerCrop().into(picture);
        }else{
            Picasso.with(mContext).load(R.drawable.ic_photo)
                    .error(R.drawable.ic_photo)
                    .resize(96, 96).centerCrop().into(picture);
        }
        Location location = new Location("POILocation");
        location.setLatitude(data.get(position).getLatitude());
        location.setLongitude(data.get(position).getLongitude());
        float distanceTo = currentLocation.distanceTo(location);

        distance.setText(formatDist(distanceTo));
        name.setText(data.get(position).getName());
        return v;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<POI> results = new ArrayList<>();
                if (orig == null)
                    orig = data;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final POI g : orig) {
                            if (g.getName().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<POI>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public static String formatDist(float meters) {
        if (meters < 1000) {
            return ((int) meters) + "m";
        } else if (meters < 10000) {
            return formatDec(meters / 1000f, 1) + "km";
        } else {
            return ((int) (meters / 1000f)) + "km";
        }
    }

    static String formatDec(float val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return front + "." + back;
    }
}
