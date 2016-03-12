package com.eugene.lysak.poiapplication.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eugene.lysak.poiapplication.Activities.MainActivity;
import com.eugene.lysak.poiapplication.R;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by zeka on 11.03.16.
 */

public class MapFragment extends SupportMapFragment {


    public MapFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setVisibleFAB(View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        myActionMenuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void setVisibleFAB(int visibleFAB)
    {
        if (getActivity() == null)return;
        MainActivity baseActivity = (MainActivity) getActivity();
        baseActivity.setVisibleFAB(visibleFAB);
    }

}
