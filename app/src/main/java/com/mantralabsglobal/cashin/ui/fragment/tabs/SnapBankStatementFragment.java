package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AvtarService;
import com.mantralabsglobal.cashin.service.BankSnapService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class SnapBankStatementFragment extends BaseBindableFragment<BankSnapService.BankSnapImage>
{

    private static final String TAG = "SnapBankStatementFragment";

    @InjectView(R.id.imageView1)
    ImageView imageView1;

    @InjectView(R.id.imageView2)
    ImageView imageView2;

    @InjectView(R.id.imageView3)
    ImageView imageView3;

    @InjectView(R.id.bankSnapView)
    ViewGroup bankSnapView;

    BankSnapService bankSnapService;
    List<ImageView> imageViewList;
    BankSnapService.BankSnapImage dirtyImage;
    ImageView imageViewClicked;
    int imageIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_snap, container, false);

        return view;
    }

    @Override
    protected View getFormView() {
        return bankSnapView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageViewList = new ArrayList<ImageView>();
        imageViewList.add(imageView1);
        imageViewList.add(imageView2);
        imageViewList.add(imageView3);
        bankSnapService = ((Application) getActivity().getApplication() ).getRestClient().getBankSnapService();

        reset(false);
    }

    @OnClick({R.id.imageView1, R.id.imageView2, R.id.imageView3})
    public void onClick(final View v) {

        imageIndex = imageViewList.indexOf(v);
        imageViewClicked = imageViewList.get(imageIndex);

        CharSequence uploadOptions[] = new CharSequence[] {"Choose from gallery", "Take a Snap"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add bank statement");
        builder.setItems(uploadOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( which == 0 )
                    pickImageFromGallery();
                else if( which == 1 )
                    launchCamera();
            }
        });
        builder.show();
    }


    @Override
    protected void onUpdate(BankSnapService.BankSnapImage updatedData, Callback<BankSnapService.BankSnapImage> saveCallback) {
        if (updatedData != null && updatedData.getFilePath() != null && updatedData.getFilePath().length() > 0) {
            TypedFile typedFile = new TypedFile("multipart/form-data", new File(updatedData.getFilePath()));
            bankSnapService.uploadBankSnapImage(typedFile, saveCallback);
        }
    }

    @Override
    protected void onCreate(BankSnapService.BankSnapImage updatedData, Callback<BankSnapService.BankSnapImage> saveCallback) {
        onUpdate(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<BankSnapService.BankSnapImage> dataCallback) {
        BankSnapService.BankSnapUtil.getBankStatamentSnapImage(dataCallback, bankSnapService, getActivity());
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(bankSnapView);
    }

    public void pickImageFromGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        getActivity().startActivityForResult(photoPickerIntent , BaseActivity.SELECT_PHOTO_FROM_GALLERY);
    }

    public void launchCamera()
    {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, true);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.STANDARD);
        intent.putExtra("FLASH", false);
        intent.putExtra("FLIP_CAMERA", false);
        getActivity().startActivityForResult(intent, BaseActivity.SELFIE_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case BaseActivity.SELECT_PHOTO_FROM_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    Log.d("Data ", imageReturnedIntent.getData() + "");
                    final Uri imageUri = imageReturnedIntent.getData();
                    beginCrop(imageUri, "selfie-cropped"+(imageIndex+1)+".jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.SELFIE_CAPTURE:
                if(resultCode == Activity.RESULT_OK){
                    String path = imageReturnedIntent.getStringExtra("file_path");
                    File file = new File(path);
                    Uri imageUri = Uri.fromFile(file);
                    beginCrop(imageUri, "selfie-cropped"+(imageIndex+1)+".jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.CROP_SELFIE:
                if (resultCode == Activity.RESULT_OK) {
                    BankSnapService.BankSnapImage bankSnapImg = new BankSnapService.BankSnapImage();
                    bankSnapImg.setFilePath(Crop.getOutput(imageReturnedIntent).getPath());
                    bindDataToForm(bankSnapImg);
                    dirtyImage = bankSnapImg;
                    save();
                } else if (resultCode == Crop.RESULT_ERROR) {
                    showToastOnUIThread(Crop.getError(imageReturnedIntent).getMessage());
                    reset(false);
                }
                break;
            default:
                Log.d("Result code", requestCode + "");
        }
    }

    private void beginCrop(Uri source, String fileName) {
        Log.d("File Name", fileName);
        Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), fileName));
        Crop.of(source, destination).asSquare().withAspect(4,5).withMaxSize(80, 100).start(getActivity(), BaseActivity.CROP_SELFIE);
    }

    @Override
    protected boolean beforeBindDataToForm(BankSnapService.BankSnapImage value, Response response) {
        if (value.getBankImage() != null && value.getBankImage().length() > 0) {
            //Log.i(TAG, "Delete picasso cache" + value.getBankImage());
            Picasso.with(getActivity()).invalidate(value.getBankImage());
            dirtyImage = null;
        }
        return false;
    }

    @Override
    public void bindDataToForm(BankSnapService.BankSnapImage value) {

        //setVisibleChildView(imageViewClicked);
        if(imageIndex < (imageViewList.size()-1))
            imageViewList.get(imageIndex+1).setVisibility(View.VISIBLE);

        if(value != null && value.getFilePath() != null && value.getFilePath().length()>0) {
            try{
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(value.getImageUri());
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageViewClicked.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                //      Log.e(TAG, e.getMessage());
            }
        }
        else if(value != null && value.getBankImage() != null && value.getBankImage().length()>0)
        {
            showProgressDialog("");
            Picasso.with(getActivity())
                    .load(value.getBankImage())
                    .fit()
                    .into(imageViewClicked, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            hideProgressDialog();
                        }

                        @Override
                        public void onError() {
                            showToastOnUIThread("Failed to load image.");
                        }
                    });
        }
    }

    @Override
    public BankSnapService.BankSnapImage getDataFromForm(BankSnapService.BankSnapImage base) {
        if(dirtyImage != null)
            return dirtyImage;
        return base;
    }

    public boolean isFormValid()
    {
        return dirtyImage != null;
    }
}