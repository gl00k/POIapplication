package com.eugene.lysak.poiapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.eugene.lysak.poiapplication.Fragments.EditPoiFragment;
import com.eugene.lysak.poiapplication.Fragments.BaseFragment;
import com.eugene.lysak.poiapplication.Fragments.DetailsPoiFragment;
import com.eugene.lysak.poiapplication.Fragments.FavoriteListPoiFragment;
import com.eugene.lysak.poiapplication.Fragments.ListPoiFragment;
import com.eugene.lysak.poiapplication.Fragments.MapFragment;
import com.eugene.lysak.poiapplication.Models.POI;
import com.eugene.lysak.poiapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zeka on 11.03.16.
 */

public class MainActivity extends AppCompatActivity implements ListPoiFragment.OnListFragmentInteractionListener,
        EditPoiFragment.OnAddFragmentInteractionListener,DetailsPoiFragment.OnDetailsFragmentInteractionListener,
        FavoriteListPoiFragment.OnFavoriteListFragmentInteractionListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    private GoogleMap mMap;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private double longitude;
    private double latitude;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                alertBox("Gps Status!!", "Your GPS is: OFF");
            } else {
                getLocation();
            }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushFragment(new EditPoiFragment());
            }
        });
        /*POI poi = new POI("Глушко 1", null, "Дом возле бювета",30.730844,46.398992);
        poi.save();

        poi = new POI("Глушко 2", null, "Тест", 30.731240, 46.401060);
        poi.save();

        poi = new POI("Новгородская 14", null, "Тест", 30.734567, 46.401849);
        poi.save();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_fragment, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        final ListPoiFragment fragment = (ListPoiFragment)manager.findFragmentByTag(ListPoiFragment.class.getName());

        searchView.setOnQueryTextListener(fragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }else if(id == R.id.action_show_all)
        {
            showAllPoiOnMap();
        }else if(id == R.id.action_favorite)
        {
            pushFragment(new FavoriteListPoiFragment());
        }

        return super.onOptionsItemSelected(item);
    }

    protected void alertBox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onListFragmentInteraction(long id) {
        DetailsPoiFragment fragment = DetailsPoiFragment.newInstance(id);
        pushFragment(fragment);
    }

    @Override
    public void onAddListFragmentInteraction() {
        getSupportFragmentManager().executePendingTransactions();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(EditPoiFragment.class.getName());
        if (fragment != null) {
            ((EditPoiFragment) fragment).savePOI(latitude, longitude);
        }
    }

    @Override
    public void onDetailFragmentAction(long id,int idButton) {

        switch (idButton)
        {
            case R.id.edit:
                EditPoiFragment fragment = EditPoiFragment.newInstance(id);
                pushFragment(fragment);
                break;
            case R.id.map:
                MapFragment mapFragment = new MapFragment();
                pushFragment(mapFragment);
                final POI poi = POI.findById(POI.class,id);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mMap = googleMap;
                                LatLng coordinate = new LatLng(poi.getLatitude(), poi.getLongitude());
                                final MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(coordinate)
                                        .title(poi.getName())
                                        .flat(true)
                                        .snippet(poi.getDescription())
                                        .draggable(true);

                                if(poi.getPhoto()!=null) {
                                    Uri uri = Uri.fromFile(new File(poi.getPhoto()));
                                    Picasso.with(MainActivity.this).load(uri)
                                            .resize(96, 96).centerCrop().into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                        }
                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });
                                }

                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12.0f));
                            }
                        });
                break;
        }

    }

    @Override
    public void onFavoriteListFragmentInteraction(long id) {
        DetailsPoiFragment fragment = DetailsPoiFragment.newInstance(id);
        pushFragment(fragment);
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            longitude = loc.getLongitude();
            latitude = loc.getLatitude();

            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            ListPoiFragment fragment = (ListPoiFragment)manager.findFragmentByTag(ListPoiFragment.class.getName());

            if(fragment!=null) {
                Location currentLocation = new Location("currentLocation");
                currentLocation.setLongitude(getCurrentLocation().longitude);
                currentLocation.setLatitude(getCurrentLocation().latitude);
                fragment.getAdapter().setCurrentLocation(currentLocation);
                fragment.getAdapter().notifyDataSetChanged();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    public void pushFragment(Fragment newFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment,newFragment.getClass().getName())
                .addToBackStack(newFragment.getClass().getName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(MapFragment.class.getName());
        if(fragment!=null) {
            manager.popBackStack();
        }else {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
            mgr.sendBroadcast(new Intent(BaseFragment.ACTION_BACK_PRESSED));
        }
    }

    public void setVisibleFAB(int visibleFAB) {
        fab.setVisibility(visibleFAB);
    }

    public void getLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        try{
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        120000, 10, locationListener);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                pushFragment(new ListPoiFragment());
            }
            //get the location by gps
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            120000, 10, locationListener);
                    if (locationManager != null) {location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                pushFragment(new ListPoiFragment());
            }

    } catch (SecurityException ex)
        {
            ex.printStackTrace();
        }
    }

    public void showAllPoiOnMap()
    {
        MapFragment mapFragment = new MapFragment();
        pushFragment(mapFragment);
        final List<POI> pois = POI.listAll(POI.class);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng coordinate = null;
                for(POI poi : pois)
                {
                    coordinate = new LatLng(poi.getLatitude(), poi.getLongitude());
                    final MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(coordinate)
                            .title(poi.getName())
                            .flat(true)
                            .snippet(poi.getDescription())
                            .draggable(true);

                    if(poi.getPhoto()!=null) {
                        Uri uri = Uri.fromFile(new File(poi.getPhoto()));
                        Picasso.with(MainActivity.this).load(uri)
                                .resize(96, 96).centerCrop().into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            }
                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });
                    }

                    mMap.addMarker(markerOptions);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12.0f));
            }
        });
    }

    public LatLng getCurrentLocation()
    {
        return new LatLng(latitude,longitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(location==null)
            getLocation();
    }
}
