package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by pk on 13/06/2015.
 */
public class YourPhotoFragment extends BaseBindableFragment<Bitmap>  {

    @InjectView(R.id.photo_viewer)
    ImageView photoViewer;

    @InjectView(R.id.vg_image_picker)
    ViewGroup imagePicker;

    @InjectView(R.id.vg_image_viewer)
    ViewGroup imageViewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_photo, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerChildView(imagePicker, View.VISIBLE);
        registerChildView(imageViewer, View.GONE);
        reset(false);
    }

    @Override
    protected void onUpdate(Bitmap updatedData, Callback<Bitmap> saveCallback) {

    }

    @Override
    protected void onCreate(Bitmap updatedData, Callback<Bitmap> saveCallback) {

    }

    @Override
    protected void loadDataFromServer(Callback<Bitmap> dataCallback) {
        dataCallback.success(null, null);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(imagePicker);
    }

    @OnClick(R.id.choose_from_gallery_button)
    public void pickImageFromGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, BaseActivity.SELECT_PHOTO_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case BaseActivity.SELECT_PHOTO_FROM_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        bindDataToForm(selectedImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    @Override
    public void bindDataToForm(Bitmap value) {

        setVisibleChildView(imageViewer);

        if(value != null)
            photoViewer.setImageBitmap(value);

    }

    @Override
    public Bitmap getDataFromForm(Bitmap base) {
        return base;
    }
}
