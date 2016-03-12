package com.eugene.lysak.poiapplication.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eugene.lysak.poiapplication.Models.POI;
import com.eugene.lysak.poiapplication.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zeka on 11.03.16.
 */

public class DetailsPoiFragment extends BaseFragment {

    @InjectView(R.id.photo)
    ImageView poiPhoto;

    @InjectView(R.id.name)
    TextView name;

    @InjectView(R.id.description)
    TextView description;

    @InjectView(R.id.edit)
    Button edit;

    @InjectView(R.id.map)
    Button map;

    private static final String ID_POI = "id_poi";

    private long mIdPoi;

    private OnDetailsFragmentInteractionListener mListener;

    public DetailsPoiFragment() {
    }

    public static DetailsPoiFragment newInstance(long idPoi) {
        DetailsPoiFragment fragment = new DetailsPoiFragment();
        Bundle args = new Bundle();
        args.putLong(ID_POI, idPoi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIdPoi = getArguments().getLong(ID_POI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.inject(this, rootView);

        POI poi = POI.findById(POI.class,mIdPoi);

        if(poi.getPhoto()!=null) {
            Uri uri = Uri.fromFile(new File(poi.getPhoto()));
            Picasso.with(getActivity()).load(uri)
                    .resize(600, 600).centerCrop().into(poiPhoto);
        }

        name.setText(poi.getName());
        description.setText(poi.getDescription());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mIdPoi,v.getId());
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mIdPoi,v.getId());
            }
        });

        return rootView;
    }


    public void onButtonPressed(long id,int idButton) {
        if (mListener != null) {
            mListener.onDetailFragmentAction(id,idButton);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailsFragmentInteractionListener) {
            mListener = (OnDetailsFragmentInteractionListener) context;
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

    public interface OnDetailsFragmentInteractionListener {
        void onDetailFragmentAction(long id,int idButton);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        myActionMenuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
