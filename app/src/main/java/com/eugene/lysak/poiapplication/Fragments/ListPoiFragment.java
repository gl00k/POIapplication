package com.eugene.lysak.poiapplication.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class ListPoiFragment extends BaseFragment implements SearchView.OnQueryTextListener{

    @InjectView(R.id.list)
    SwipeMenuListView listView;

    private OnListFragmentInteractionListener mListener;
    private List<POI> listPoi;
    private PoiListAdapter adapter;
    private Filter filter;

    public ListPoiFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this,rootView);

        listPoi = POI.listAll(POI.class);
        LatLng currentLocation = getCurrentLocation();

        Collections.sort(listPoi, new SortPOI(currentLocation));

        adapter = new PoiListAdapter(getActivity(),listPoi,getCurrentLocation());
        listView.setAdapter(adapter);

        listView.setMenuCreator(creator);

        listView.setTextFilterEnabled(true);

        filter = adapter.getFilter();

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
                        POI currentPoi = POI.findById(POI.class,listPoi.get(position).getId());
                        currentPoi.setFavorite(true);
                        currentPoi.save();
                        break;
                    case 1:
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle(getResources().getString(R.string.remove_this_POI));
                        adb.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                POI currentPoi = POI.findById(POI.class,listPoi.get(position).getId());
                                currentPoi.delete();
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
            mListener.onListFragmentInteraction(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            filter.filter("");
        } else {
            filter.filter(newText);
        }
        return true;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(long id);
    }

    protected void onReceiveBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        setVisibleFAB(View.VISIBLE);
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "like" item
            SwipeMenuItem likeItem = new SwipeMenuItem(
                    getActivity());
            likeItem.setBackground(new ColorDrawable(Color.rgb(34, 128,
                    58)));
            likeItem.setWidth(dp2px(90));
            likeItem.setIcon(R.drawable.ic_favorite_24dp);
            menu.addMenuItem(likeItem);

            // create "delete" item
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

    public PoiListAdapter getAdapter()
    {
        return adapter;
    }
}
