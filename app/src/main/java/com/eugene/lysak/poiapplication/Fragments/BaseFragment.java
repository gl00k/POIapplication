package com.eugene.lysak.poiapplication.Fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.eugene.lysak.poiapplication.Activities.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zeka on 11.03.16.
 */

public class BaseFragment extends Fragment {

    public static final String ACTION_BACK_PRESSED = "ACTION_BACK_PRESSED";
    protected MainActivity mActivity;
    protected LocalBroadcastManager mManager;
    protected IntentFilter mFilter = new IntentFilter(ACTION_BACK_PRESSED);
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveBackPressed();
        }
    };

    protected void onReceiveBackPressed() {

    }

    @Override
    public void onPause() {
        mManager.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mManager.registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    protected void pushFragment(android.support.v4.app.Fragment fragment) {
        if (getActivity() == null)return;
        MainActivity baseActivity = (MainActivity) getActivity();
        baseActivity.pushFragment(fragment);
    }

    protected void setVisibleFAB(int visibleFAB)
    {
        if (getActivity() == null)return;
        MainActivity baseActivity = (MainActivity) getActivity();
        baseActivity.setVisibleFAB(visibleFAB);
    }

    protected LatLng getCurrentLocation()
    {
        if (getActivity() == null)return null;
        MainActivity baseActivity = (MainActivity) getActivity();
        return baseActivity.getCurrentLocation();
    }

}
