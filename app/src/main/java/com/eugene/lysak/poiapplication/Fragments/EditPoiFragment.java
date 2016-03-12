package com.eugene.lysak.poiapplication.Fragments;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.eugene.lysak.poiapplication.Models.POI;
import com.eugene.lysak.poiapplication.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zeka on 11.03.16.
 */

public class EditPoiFragment extends BaseFragment implements View.OnClickListener{

    @InjectView(R.id.photo)
    ImageView poiPhoto;

    @InjectView(R.id.name)
    EditText name;

    @InjectView(R.id.description)
    EditText description;

    @InjectView(R.id.save)
    Button save;

    protected static final int REQUEST_CAMERA = 0;
    protected static final int SELECT_FILE = 1;
    private Uri fileUri;
    private String filePath;
    private boolean editFlag = false;
    private String folderToSave;
    private POI editPoi;

    private static final String ID_POI = "id_poi";

    private long mIdPoi;

    private OnAddFragmentInteractionListener mListener;

    public EditPoiFragment() {
    }

    public static EditPoiFragment newInstance(long idPoi) {
        EditPoiFragment fragment = new EditPoiFragment();
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
            editFlag = true;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onLocationRequest() {
        if (mListener != null) {
            mListener.onAddListFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddFragmentInteractionListener) {
            mListener = (OnAddFragmentInteractionListener) context;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_poi, container, false);
        ButterKnife.inject(this, rootView);

        if(editFlag)
        {
            editPoi = POI.findById(POI.class,mIdPoi);

            if(editPoi.getPhoto()!=null) {
                Uri uri = Uri.fromFile(new File(editPoi.getPhoto()));
                Picasso.with(getActivity()).load(uri)
                        .resize(600, 600).centerCrop().into(poiPhoto);
            }

            name.setText(editPoi.getName());
            description.setText(editPoi.getDescription());
        }

        folderToSave = getActivity().getFilesDir().toString();
        save.setOnClickListener(this);
        poiPhoto.setOnClickListener(this);

        return rootView;
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
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save:
                if(!TextUtils.isEmpty(name.getText())&&!TextUtils.isEmpty(description.getText())) {
                    if(editFlag)
                    {
                        editPoi.setName(name.getText().toString());
                        editPoi.setDescription(description.getText().toString());
                        editPoi.setLatitude(getCurrentLocation().latitude);
                        editPoi.setLongitude(getCurrentLocation().longitude);
                        if(filePath!=null)
                            editPoi.setPhoto(filePath);

                        editPoi.save();
                        getActivity().onBackPressed();
                    }else {
                        onLocationRequest();
                    }
                }else{
                    cantBeEmpty();
                }
                break;
            case R.id.photo:
                selectImage();
                break;
        }
    }

    private void cantBeEmpty()
    {
        StringBuffer builder = new StringBuffer();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(getResources().getString(R.string.cant_empty));
        adb.setPositiveButton(getResources().getString(R.string.yes), null);
        adb.show();
    }


    public interface OnAddFragmentInteractionListener {
        void onAddListFragmentInteraction();
    }

    public void savePOI(double latitude,double longitude)
    {
        POI poi = new POI(name.getText().toString(), filePath, description.getText().toString(), longitude, latitude,false);
        poi.save();
        getActivity().onBackPressed();
    }

    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.make_photo),
                getResources().getString(R.string.choice_gallery_image),
                getResources().getString(R.string.cancel_) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.make_shot))) {

                    String fileName = "camera.jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                    fileUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getResources().getString(R.string.choice_gallery_image))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getResources().getString(R.string.choice_file)),
                            SELECT_FILE);
                } else if (items[item].equals(getResources().getString(R.string.cancel_))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                SavePicture();
            } else if (requestCode == SELECT_FILE) {
                fileUri = data.getData();
                SavePicture();
            }
        }
    }

    private String SavePicture()
    {
        OutputStream fOut = null;
        try {
            String fname = "img_" + System.currentTimeMillis() + ".jpg";
            File file = new File(folderToSave, fname);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
            fOut = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            filePath = file.getPath();
            fOut.flush();
            fOut.close();
            Picasso.with(getActivity()).load(fileUri)
                    .resize(600, 600).centerCrop().into(poiPhoto);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
        return "";
    }
}
