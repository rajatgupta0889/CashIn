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
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.utils.CameraUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
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

        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("outputX", 512);
        photoPickerIntent.putExtra("outputY", 512);
        photoPickerIntent.putExtra("aspectX", 1);
        photoPickerIntent.putExtra("aspectY", 1);
        photoPickerIntent.putExtra("scale", true);

        startActivityForResult(photoPickerIntent, BaseActivity.SELECT_PHOTO_FROM_GALLERY);
    }

    @OnClick(R.id.launch_camera_button)
    public void launchCamera()
    {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, true);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.FFC);
        getActivity().startActivityForResult(intent, BaseActivity.SELFIE_CAPTURE);
    }

    @OnClick(R.id.edit_selfie_button)
    public void editSelfie(){
        setVisibleChildView(imagePicker);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case BaseActivity.SELECT_PHOTO_FROM_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    final Uri imageUri = imageReturnedIntent.getData();
                    beginCrop(imageUri, "selfie-cropped.jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.SELFIE_CAPTURE:
                if(resultCode == Activity.RESULT_OK){
                    String path = imageReturnedIntent.getStringExtra("file_path");
                    File file = new File(path);
                    Uri imageUri = Uri.fromFile(file);
                    beginCrop(imageUri, "selfie-cropped.jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.CROP_SELFIE:
                if (resultCode == Activity.RESULT_OK) {
                    bindDataToForm(Uri.fromFile(new File(Crop.getOutput(imageReturnedIntent).getPath())));
                } else if (resultCode == Crop.RESULT_ERROR) {
                    showToastOnUIThread(Crop.getError(imageReturnedIntent).getMessage());
                    reset(false);
                }
                break;

        }
    }

    private void beginCrop(Uri source, String fileName) {

        Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), fileName));
        Crop.of(source, destination).asSquare().withAspect(3,4).withMaxSize(800, 1200).start(getActivity(), BaseActivity.CROP_SELFIE);
    }


    public void bindDataToForm(Uri imageUri) {
        try{

            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            bindDataToForm(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
