package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantralabsglobal.cashin.BuildConfig;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.OCRServiceProvider;
import com.mantralabsglobal.cashin.service.PanCardService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.fragment.camera.CwacCameraFragment;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.ImageUtils;
import com.mantralabsglobal.cashin.utils.PANUtils;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.soundcloud.android.crop.Crop;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class PANCardFragment extends BaseBindableFragment<PanCardService.PanCardDetail> implements OCRServiceProvider<PanCardService.PanCardDetail> {

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

   /* @NotEmpty
    @InjectView(R.id.cc_father_name)
    public CustomEditText fatherName;

    @NotEmpty
    @InjectView(R.id.cc_dob)
    public BirthDayView dob;*/

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
    protected View getFormView() {
        return vg_form;
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
        intent.putExtra(CwacCameraFragment.SHOW_BOUNDS, true);
        intent.putExtra("FLASH", false);
        intent.putExtra("FLIP_CAMERA", false);
        intent.putExtra(CwacCameraFragment.ASPECT_RATIO, Double.parseDouble("0.66666666"));
        intent.putExtra(CwacCameraFragment.SHOW_INFO, getResources().getString(R.string.position_card_inside_frame));

        getActivity().startActivityForResult(intent, BaseActivity.IMAGE_CAPTURE_PAN_CARD);
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

        if (requestCode == BaseActivity.IMAGE_CAPTURE_PAN_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                showToastOnUIThread(data.getStringExtra("file_path"));
                beginCrop( Uri.fromFile(new File(data.getStringExtra("file_path") )));

                Log.d("PANCardFragment", "onActivityResult, resultCode " + resultCode + " filepath = " +data.getStringExtra("file_path"));
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                reset(false);
            }
        }  else if (requestCode == BaseActivity.IMAGE_CROP_PAN_CARD) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), "pan-card-cropped.jpg"));
        Crop.of(source, destination).asSquare().withAspect(3,2).start(getActivity(), BaseActivity.IMAGE_CROP_PAN_CARD);
    }

    @Override
    protected void preProcessOCRData(PanCardService.PanCardDetail detail) {
        String newName = PANUtils.getName(detail.getContentarr(), 5);
        String newPAN = PANUtils.getPANNumber(detail.getContentarr(), 5);
        if(StringUtils.isNotEmpty(newName))
            detail.setName(newName);
        if(StringUtils.isNoneEmpty(newPAN))
            detail.setPanNumber(newPAN);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            showProgressDialog(getString(R.string.processing_image));
            Bitmap binary = new ImageUtils().binarize( BitmapFactory.decodeFile(Crop.getOutput(result).getPath()));
            uploadImageToServerForOCR(binary, PANCardFragment.this);
            if (BuildConfig.DEBUG) {
                showImageDialog(binary);
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            hideProgressDialog();
            showToastOnUIThread(Crop.getError(result).getMessage());
            reset(false);
        }
    }

    @Override
    public void bindDataToForm(PanCardService.PanCardDetail value) {
        setVisibleChildView(vg_form);
        if(value != null)
        {
            name.setText(value.getName());
            //fatherName.setText(value.getSonOf());
            panNumber.setText(value.getPanNumber());
            //dob.setText(value.getDob());
        }
    }

    @Override
    public PanCardService.PanCardDetail getDataFromForm(PanCardService.PanCardDetail base) {
        if(base == null)
            base = new PanCardService.PanCardDetail();

        //base.setSonOf(fatherName.getText().toString());
        base.setName(name.getText().toString());
        //base.setDob(dob.getText().toString());
        base.setPanNumber(panNumber.getText().toString());

        return base;
    }

    @Override
    public void getDetailFromImage(CardImage image, Callback<PanCardService.PanCardDetail> callback) {
        panCardServiceOCR.getPanCardDetailFromImage(image, callback);
    }

}
