package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.zxing.BarcodeFormat;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.utils.AadharDAO;
import com.mantralabsglobal.cashin.service.AadharService;
import com.mantralabsglobal.cashin.ui.activity.scanner.ScannerActivity;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mantralabsglobal.cashin.ui.view.SonOfSpinner;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class AadharCardFragment extends BaseFragment implements Bindable<AadharService.AadharDetail>
{

    private AadharService.AadharDetail aadharDetail;

    @InjectView(R.id.cc_name)
    CustomEditText name;

    @InjectView(R.id.cc_address)
    CustomEditText address;

    @InjectView(R.id.cc_aadhar)
    CustomEditText aadharNumber;

    @InjectView(R.id.cs_gender)
     CustomSpinner gender;

    @InjectView(R.id.cs_sonOf)
     SonOfSpinner relation;

    @InjectView(R.id.ib_launchScanner)
     ImageButton btn_scanner;

    @InjectView(R.id.bt_edit_aadhar_detail)
    Button btn_edit_aadhar_detail;

    @InjectView(R.id.fab_launchScanner)
     FloatingActionButton fab_launchScanner;

    @InjectView(R.id.ll_aadhar_camera)
     ViewGroup vg_camera;

    @InjectView(R.id.rl_aadhar_detail)
     ViewGroup vg_form;

    @InjectView(R.id.btn_save)
    Button btnSave;

    @InjectView(R.id.btn_cancel)
    Button btnCancel;

    static final int SCAN_AADHAR_CARD = 99;

    private AadharService.AadharDetail aadharDetailServer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        return inflater.inflate(R.layout.fragment_aadhar_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gender.setAdapter(getGenderAdapter());

        relation.setAdapter(relation.getAdapter());

        registerChildView(vg_camera, View.VISIBLE);
        registerChildView(vg_form, View.GONE);
        registerFloatingActionButton(fab_launchScanner, vg_form);

        Log.d("AadharCardFragment", "On view created");
    }


    @OnClick(R.id.bt_edit_aadhar_detail)
    public void loadAadharForm()
    {
        setVisibleChildView(vg_form);
        bindDataToForm(aadharDetail);
    }

    @OnClick( {R.id.ib_launchScanner, R.id.fab_launchScanner})
    public void loadAadharScanner() {
        ArrayList<String> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE.toString());
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        intent.putExtra("FORMATS", formatList);
        intent.putExtra("FLASH", false);
        intent.putExtra("AUTO_FOCUS", true);
        getActivity().startActivityForResult(intent, SCAN_AADHAR_CARD);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AadharCardFragment", "onActivityResult: " + this);
        Log.d("AadharCardFragment", "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == SCAN_AADHAR_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("AadharCardFragment", "onActivityResult: " + data.getStringExtra("aadhar_xml"));
                aadharDetail = AadharDAO.getAadharDetailFromXML(data.getStringExtra("aadhar_xml"));
                loadAadharForm();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*Log.d("AadharCardFragment", "On resume");
        Log.d("AadharCardFragment", "aadharDetail: " + aadharDetail);

        SharedPreferences preferences = getActivity().getSharedPreferences(AadharCardFragment.class.getName(), Context.MODE_PRIVATE);
        String aadhar_xml = preferences.getString("aadhar_xml", null);
        if(aadhar_xml != null)
            aadharDetail = AadharDAO.getAadharDetailFromXML(aadhar_xml);
        if(aadharDetail != null)
            loadAadharForm();
        else
            setVisibleChildView(vg_camera);

*/
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Log.d("AadharCardFragment", "On attach");
    }

    @Override
    public void bindDataToForm(AadharService.AadharDetail value) {
        //TODO: Replace with form binding
        if(aadharDetail!= null) {
            name.setText(aadharDetail.getName());
            address.setText(aadharDetail.getAddress());
            aadharNumber.setText(aadharDetail.getAadharNumber());
            gender.setSelection(getGenderAdapter().getPosition(aadharDetail.getGender()));
            relation.setSelection(relation.getRelationAdapter().getPosition(aadharDetail.getSonOf()));

        }
    }

    @Override
    public AadharService.AadharDetail getDataFromForm(AadharService.AadharDetail detail) {
        return detail;
    }

    @OnClick(R.id.btn_save)
    protected void onSave()
    {
        if(canSave()) {
            AadharService aadharService = ((Application) getActivity().getApplication()).getRestClient().getAadharService();
            AadharService.AadharDetail aadharDetail = aadharDetailServer;
            if(aadharDetail == null)
                aadharDetail = new AadharService.AadharDetail();
            aadharService.setAadharDetail(getDataFromForm(aadharDetail), new Callback<AadharService.AadharDetail>() {
                @Override
                public void success(AadharService.AadharDetail aadharDetail, Response response) {
                    showToastOnUIThread(getString(R.string.save_sucess));
                }

                @Override
                public void failure(RetrofitError error) {
                    showToastOnUIThread(error.getMessage());
                }
            });
        }
    }

}
