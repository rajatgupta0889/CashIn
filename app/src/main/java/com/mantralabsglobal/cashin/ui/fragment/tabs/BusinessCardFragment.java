package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.BusinessCardService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BusinessCardFragment extends BaseBindableFragment<BusinessCardService.BusinessCardDetail> {

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

    @InjectView(R.id.fab_launchScanner)
    public FloatingActionButton fab_launchScanner;

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
        registerFloatingActionButton(fab_launchScanner, vg_form);

        reset(false);
    }
    @Override
    protected View getFormView() {
        return vg_form;
    }
    @Override
    protected void onUpdate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.updateBusinessCardDetail(updatedData,saveCallback);
    }

    @Override
    protected void onCreate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.createBusinessCardDetail(updatedData,saveCallback);
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
}
