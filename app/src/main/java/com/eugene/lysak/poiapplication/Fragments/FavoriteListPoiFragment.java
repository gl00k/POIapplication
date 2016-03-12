package com.eugene.lysak.poiapplication.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.eugene.lysak.poiapplication.Models.POI;
import com.eugene.lysak.poiapplication.R;
import com.eugene.lysak.poiapplication.Utils.PoiListAdapter;
import com.eugene.lysak.poiapplication.Utils.SortPOI;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zeka on 11.03.16.
 */

public class FavoriteListPoiFragment extends BaseFragment{

    @InjectView(R.id.list)
    SwipeMenuListView listView;

    private OnFavoriteListFragmentInteractionListener mListener;
    private List<POI> listPoi;
    private PoiListAdapter adapter;

    public FavoriteListPoiFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this,rootView);

        listPoi = POI.findWithQuery(POI.class,"Select * from POI where favorite = ?","1");

        LatLng currentLocation = getCurrentLocation();
        Collections.sort(listPoi, new SortPOI(currentLocation));

        adapter = new PoiListAdapter(getActivity(),listPoi,getCurrentLocation());
        listView.setAdapter(adapter);

        listView.setMenuCreator(creator);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickItem(id);
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle(getResources().getString(R.string.remove_this_POI));
                        adb.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                POI currentPoi = POI.findById(POI.class,listPoi.get(position).getId());
                                currentPoi.setFavorite(false);
                                currentPoi.save();
                                listPoi.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        adb.setNegativeButton(getResources().getString(R.string.no), null);
                        adb.show();
                        break;
                }
                return false;
            }
        });
        return rootView;
    }

    public void onClickItem(long id) {
        if (mListener != null) {
            mListener.onFavoriteListFragmentInteraction(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteListFragmentInteractionListener) {
            mListener = (OnFavoriteListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFavoriteListFragmentInteractionListener {
        void onFavoriteListFragmentInteraction(long id);
    }

    protected void onReceiveBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(ListPoiFragment.class.getName());
        if(fragment!=null) {
            manager.popBackStack();
        }else{
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            pushFragment(new ListPoiFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setVisibleFAB(View.INVISIBLE);
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {

            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(255,
                    0, 0)));
            deleteItem.setWidth(dp2px(90));
            deleteItem.setIcon(R.drawable.ic_delete_24dp);
            menu.addMenuItem(deleteItem);
        }
    };

    private int dp2px(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setVisible(false);
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_favorite);
        favoriteMenuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
