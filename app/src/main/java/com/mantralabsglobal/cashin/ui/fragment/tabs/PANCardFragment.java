package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PanCardService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.camera.CameraActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.SonOfSpinner;
import com.mantralabsglobal.cashin.utils.CameraUtils;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class PANCardFragment extends BaseBindableFragment<PanCardService.PanCardDetail>  {

    private static final int IMAGE_CAPTURE_AADHAR_CARD = 199;

    @InjectView(R.id.vg_pan_card_scan)
    public ViewGroup vg_scan;
    @InjectView(R.id.vg_pan_card_form)
    public ViewGroup vg_form;

    @NotEmpty
    @InjectView(R.id.cc_name)
    public CustomEditText name;

    @NotEmpty
    @InjectView(R.id.cc_pan)
    public CustomEditText panNumber;

    @NotEmpty
    @InjectView(R.id.cc_father_name)
    public CustomEditText fatherName;

    @NotEmpty
    @InjectView(R.id.cc_dob)
    public BirthDayView dob;

    PanCardService panCardService;
    PanCardService panCardServiceOCR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_pan_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        panCardService = ((Application)getActivity().getApplication()).getRestClient().getPanCardService();
        panCardServiceOCR = ((Application)getActivity().getApplication()).getRestClient().getPanCardServiceOCR();
        //SonOfSpinner relation = (SonOfSpinner) view.findViewById(R.id.cs_sonOf);
        registerChildView(vg_form, View.GONE);
        registerChildView(vg_scan, View.VISIBLE);
        registerFloatingActionButton((FloatingActionButton) getCurrentView().findViewById(R.id.fab_launch_camera), getCurrentView().findViewById(R.id.vg_pan_card_form));

        reset(false);
    }

    @Override
    protected void onUpdate(PanCardService.PanCardDetail updatedData, Callback<PanCardService.PanCardDetail> saveCallback) {
        panCardService.updatePanCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(PanCardService.PanCardDetail updatedData, Callback<PanCardService.PanCardDetail> saveCallback) {
        panCardService.createPanCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<PanCardService.PanCardDetail> dataCallback) {
        panCardService.getPanCardDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vg_scan);
    }

    @OnClick( {R.id.ib_launch_camera, R.id.fab_launch_camera})
    public void launchCamera() {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, false);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.STANDARD);
        getActivity().startActivityForResult(intent, IMAGE_CAPTURE_AADHAR_CARD);
    }

    @OnClick(R.id.btn_pan_card_detail_form)
    public void onClick(View v) {
        bindDataToForm(new PanCardService.PanCardDetail());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AadharCardFragment", "onActivityResult: " + this);
        Log.d("AadharCardFragment", "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == IMAGE_CAPTURE_AADHAR_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                showToastOnUIThread(data.getStringExtra("file_path"));
                beginCrop( Uri.fromFile(new File(data.getStringExtra("file_path") )));
                Log.d("PANCardFragment", "onActivityResult, resultCode " + resultCode + " filepath = " +data.getStringExtra("file_path"));
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                reset(false);
            }
        }  else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), "pan-card-cropped.jpg"));
        Crop.of(source, destination).asSquare().withAspect(4,3).start(getActivity());
    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            showProgressDialog(getString(R.string.processing_image));
            CameraUtils.createBlackAndWhite(Crop.getOutput(result).getPath(), new CameraUtils.Listener() {
                @Override
                public void onComplete(final Bitmap bmp) {
                    getActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    uploadImageToServerForOCR(bmp);
                                    /*Dialog dialog = new Dialog(getActivity());
                                    dialog.setContentView(R.layout.dialog_image_preview);
                                    ImageView imgView=(ImageView)dialog.findViewById(R.id.iv_image);
                                    imgView.setImageBitmap(bmp);
                                    hideProgressDialog();
                                    dialog.show();*/
                                }
                            }
                    );
                }
            });

        } else if (resultCode == Crop.RESULT_ERROR) {
            hideProgressDialog();
            showToastOnUIThread(Crop.getError(result).getMessage());
            reset(false);
        }
    }

    private void uploadImageToServerForOCR(final Bitmap bmp) {
        AsyncTask<Bitmap, Void, Void> asynTask = new AsyncTask<Bitmap, Void, Void>() {
            @Override
            protected Void doInBackground(Bitmap... params) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                PanCardService.PanCardImage panCardImage = new PanCardService.PanCardImage();
                panCardImage.setBase64encodedImage(encoded);
                panCardServiceOCR.getPanCardDetailFromImage(panCardImage, new Callback<PanCardService.PanCardDetail>() {
                    @Override
                    public void success(PanCardService.PanCardDetail panCardDetail, Response response) {
                        hideProgressDialog();
                        bindDataToForm(panCardDetail);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgressDialog();
                        Snackbar snackbar = Snackbar
                                .make((CoordinatorLayout) getCurrentView(), "Failed to process PAN card Image. Error: " + error.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showProgressDialog(getString(R.string.processing_image));
                                        uploadImageToServerForOCR(bmp);
                                    }
                                });
                        snackbar.show();
                    }
                });
                return null;
            }
        }.execute(bmp);
    }

    @Override
    public void bindDataToForm(PanCardService.PanCardDetail value) {
        setVisibleChildView(vg_form);
        if(value != null)
        {
            name.setText(value.getName());
            fatherName.setText(value.getSonOf());
            panNumber.setText(value.getPanNumber());
            dob.setText(value.getDob());
        }
    }

    @Override
    public PanCardService.PanCardDetail getDataFromForm(PanCardService.PanCardDetail base) {
        if(base == null)
            base = new PanCardService.PanCardDetail();

        base.setSonOf(fatherName.getText().toString());
        base.setName(name.getText().toString());
        base.setDob(dob.getText().toString());
        base.setPanNumber(panNumber.getText().toString());

        return base;
    }
}
