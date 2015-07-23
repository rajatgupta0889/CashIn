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
import android.widget.Button;

import com.mantralabsglobal.cashin.BuildConfig;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.BusinessCardService;
import com.mantralabsglobal.cashin.service.OCRServiceProvider;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.MainActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.fragment.camera.CwacCameraFragment;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.CameraUtils;
import com.mantralabsglobal.cashin.utils.ImageUtils;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BusinessCardFragment extends BaseBindableFragment<BusinessCardService.BusinessCardDetail>implements OCRServiceProvider<BusinessCardService.BusinessCardDetail> {

    private static final String TAG = "BusinessCardFragment";
    @InjectView(R.id.ll_business_card_snap)
    public ViewGroup vg_snap;
    @InjectView(R.id.ll_business_card_detail)
    public ViewGroup vg_form;
    @InjectView(R.id.enterWorkDetailsButton)
    public Button btn_enter_details;

    @NotEmpty
    @InjectView(R.id.cc_employer_name)
    public CustomEditText employerName;

    @NotEmpty
    @Email
    @InjectView(R.id.cc_work_email_id)
    public CustomEditText emailId;

    @InjectView(R.id.fab_launch_camera)
    public FloatingActionButton fab_launchCamera;

    @NotEmpty
    @InjectView(R.id.cc_work_addess)
    public CustomEditText workAddress;

    private BusinessCardService businessCardService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_business_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        businessCardService = ((Application)getActivity().getApplication()).getRestClient().getBusinessCardService();

        registerChildView(vg_snap, View.VISIBLE);
        registerChildView(vg_form, View.GONE);
        registerFloatingActionButton(fab_launchCamera, vg_form);

        reset(false);
    }
    @Override
    protected View getFormView() {
        return vg_form;
    }
    @Override
    protected void onUpdate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.updateBusinessCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.createBusinessCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<BusinessCardService.BusinessCardDetail> dataCallback) {
        businessCardService.getBusinessCardDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vg_snap);
    }


    @OnClick(R.id.enterWorkDetailsButton)
    public void onEnterDetailClick() {

        bindDataToForm(new BusinessCardService.BusinessCardDetail());
    }

    @Override
    public void bindDataToForm(BusinessCardService.BusinessCardDetail value) {
        setVisibleChildView(vg_form);
        if(value != null)
        {
            employerName.setText(value.getEmployerName());
            workAddress.setText(value.getAddress());
            emailId.setText(value.getEmail());
        }
    }

    @Override
    public BusinessCardService.BusinessCardDetail getDataFromForm(BusinessCardService.BusinessCardDetail base) {
        if(base == null)
            base = new BusinessCardService.BusinessCardDetail();

        base.setEmployerName(employerName.getText().toString());
        base.setAddress(workAddress.getText().toString());
        base.setEmail(emailId.getText().toString());

        return base;
    }

    @OnClick( {R.id.ib_launch_camera, R.id.fab_launch_camera})
    public void launchCamera() {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, false);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.STANDARD);
        intent.putExtra(CwacCameraFragment.SHOW_BOUNDS, true);
        intent.putExtra(CwacCameraFragment.ASPECT_RATIO, Double.parseDouble("0.66666666"));
        intent.putExtra(CwacCameraFragment.SHOW_INFO, getResources().getString(R.string.position_card_inside_frame));

        getActivity().startActivityForResult(intent, BaseActivity.IMAGE_CAPTURE_BUSINESS_CARD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + this);
        Log.d(TAG, "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == BaseActivity.IMAGE_CAPTURE_BUSINESS_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                showToastOnUIThread(data.getStringExtra("file_path"));

                Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), "business-card-cropped.jpg"));
                Crop.of(Uri.fromFile(new File(data.getStringExtra("file_path")))
                        , destination).asSquare().withAspect(3,2).start(getActivity(), BaseActivity.IMAGE_CROP_BUSINESS_CARD);

                Log.d(TAG, "onActivityResult, resultCode " + resultCode + " filepath = " +data.getStringExtra("file_path"));
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                reset(false);
            }
        }  else if (requestCode == BaseActivity.IMAGE_CROP_BUSINESS_CARD) {

            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            showProgressDialog(getString(R.string.processing_image));
            Bitmap binary = new ImageUtils().binarize( BitmapFactory.decodeFile(Crop.getOutput(result).getPath()));
            uploadImageToServerForOCR(binary, BusinessCardFragment.this);
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
    public void getDetailFromImage(CardImage image, Callback<BusinessCardService.BusinessCardDetail> callback) {
        businessCardService.getBusinessCardDetailFromImage(image, callback);
    }

}
