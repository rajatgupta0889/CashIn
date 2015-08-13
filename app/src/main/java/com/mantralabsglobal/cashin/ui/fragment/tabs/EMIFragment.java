package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.EMIService;
import com.mantralabsglobal.cashin.service.IncomeService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.fragment.adapter.EMIAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;


public class EMIFragment extends BaseBindableListFragment<EMIService.EMI> {

    EMIService emiService;
    EMIService.EMI[] emiData;
    EMIService.EMI emi;
    EditText amountText, descriptionText;

    @InjectView(R.id.vg_form)
    ViewGroup vg_form;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_emi, container, false);

        final ListView emiListView = (ListView) view.findViewById(R.id.emi_list);
        amountText = (EditText) view.findViewById(R.id.emi_amount_text);
        descriptionText = (EditText) view.findViewById(R.id.emi_description_text);

        emiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emi = new EMIService.EMI();
                emi.setEmiAmount(Integer.parseInt(amountText.getText().toString()));
                emi.setEMIDescription(amountText.getText().toString());
                emiData[emiData.length] = emi;
            }
        });

        if( emiData != null && emiData.length>0 ) {
            EMIAdapter emiAdapter = new EMIAdapter(getActivity(), R.layout.fragment_emi_row, emiData);
            emiListView.setAdapter(emiAdapter);
        }
        return view;
    }

    @Override
    protected View getFormView() {
        return vg_form;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emiService = ((Application)getActivity().getApplication()).getRestClient().getEmiService();
        reset(false);

    }

    @Override
    protected void onUpdate(List<EMIService.EMI> updatedData, Callback<List<EMIService.EMI>> saveCallback) {
        onCreate(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(List<EMIService.EMI> updatedData, Callback<List<EMIService.EMI>> saveCallback) {
        emiService.createEMI(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<List<EMIService.EMI>> dataCallback) {
        emiService.getEMIDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {


    }


    @Override
    public void bindDataToForm(List<EMIService.EMI> value) {

    }

    @Override
    public List<EMIService.EMI> getDataFromForm(List<EMIService.EMI> base) {
        if(base == null) {
            base = new ArrayList<>();
        }
        while(base.size()<3)
        {
            base.add(new EMIService.EMI());
        }


        return base;
    }

}
