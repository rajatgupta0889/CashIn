package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ImageView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.camera.CameraActivity;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.SonOfSpinner;
import com.mantralabsglobal.cashin.utils.CameraUtils;

import butterknife.OnClick;

/**
 * Created by pk on 13/06/2015.
 */
public class PANCardFragment extends BaseFragment  {

    private static final int IMAGE_CAPTURE_AADHAR_CARD = 199;
    private BirthDayView dobEditText;

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

        SonOfSpinner relation = (SonOfSpinner) view.findViewById(R.id.cs_sonOf);
        registerChildView(getCurrentView().findViewById(R.id.vg_pan_card_form), View.GONE);
        registerChildView(getCurrentView().findViewById(R.id.vg_pan_card_scan), View.VISIBLE);
        registerFloatingActionButton((FloatingActionButton)getCurrentView().findViewById(R.id.fab_launch_camera), getCurrentView().findViewById(R.id.vg_pan_card_form));

    }

    @OnClick( {R.id.ib_launch_camera, R.id.fab_launch_camera})
    public void launchCamera() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        getActivity().startActivityForResult(intent, IMAGE_CAPTURE_AADHAR_CARD);
    }

    @OnClick(R.id.btn_pan_card_detail_form)
    public void onClick(View v) {

        setVisibleChildView(getCurrentView().findViewById(R.id.vg_pan_card_form));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AadharCardFragment", "onActivityResult: " + this);
        Log.d("AadharCardFragment", "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == IMAGE_CAPTURE_AADHAR_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap;
                try
                {
                    //bitmap = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), new Uri.Builder().path(data.getStringExtra("file_path")).build());
                    showProgressDialog(getString(R.string.processing_image));
                    CameraUtils.createBlackAndWhite(data.getStringExtra("file_path"), new CameraUtils.Listener() {
                        @Override
                        public void onComplete(final Bitmap bmp) {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialog dialog = new Dialog(getActivity());
                                            dialog.setContentView(R.layout.dialog_image_preview);
                                            ImageView imgView=(ImageView)dialog.findViewById(R.id.iv_image);
                                            imgView.setImageBitmap(bmp);
                                            hideProgressDialog();
                                            dialog.show();
                                        }
                                    }
                            );
                        }
                    });



                } catch (Exception e)
                {
                    showToastOnUIThread("Failed to load" + e.getMessage());
                    Log.d("PANCardFragment", "Failed to load", e);
                }
                showToastOnUIThread(data.getStringExtra("file_path"));
                Log.d("PANCardFragment", "onActivityResult, resultCode " + resultCode + " filepath = " +data.getStringExtra("file_path"));

            }
        }
    }

}
