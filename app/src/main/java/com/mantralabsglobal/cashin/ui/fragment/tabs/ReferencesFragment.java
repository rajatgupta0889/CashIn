package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.ReferenceService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.ContactPickerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class ReferencesFragment extends BaseBindableListFragment<ReferenceService.Reference>
{
    private static final String TAG ="ReferencesFragment" ;
    @InjectView(R.id.vg_reference_detail)
     ViewGroup vgReferenceDetail;

    @InjectView(R.id.vg_select_reference)
    ViewGroup vgSelectReference;

    @InjectView(R.id.fab_select_reference)
    FloatingActionButton fab_selectReference;

    ReferenceService referenceService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        return inflater.inflate(R.layout.fragment_refrences, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        referenceService = ((Application)getActivity().getApplication()).getRestClient().getReferenceService();

        registerChildView(vgSelectReference, View.VISIBLE);
        registerChildView(vgReferenceDetail, View.GONE);
        registerFloatingActionButton(fab_selectReference, vgSelectReference);

        reset(false);

        Log.d(TAG, "On view created");
    }

    @Override
    protected void onUpdate(List<ReferenceService.Reference> updatedData, Callback<List<ReferenceService.Reference>> saveCallback) {
        referenceService.addReferences(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(List<ReferenceService.Reference> updatedData, Callback<List<ReferenceService.Reference>> saveCallback) {
        referenceService.addReferences(updatedData,saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<List<ReferenceService.Reference>> dataCallback) {
        referenceService.getReferences(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vgSelectReference);
    }

    @Override
    protected View getFormView() {
        return vgReferenceDetail;
    }


    @OnClick( {R.id.fab_select_reference})
    public void loadReferences() {
        Intent intent = new Intent(getActivity(), ContactPickerActivity.class);
        intent.putExtra("ContactCount", 3);
        getActivity().startActivityForResult(intent, BaseActivity.CONTACT_PICKER);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + this);
        Log.d(TAG, "requestCode " + requestCode + " , resultCode=" + resultCode);

    }

    @Override
    public void bindDataToForm(List<ReferenceService.Reference> value) {
        setVisibleChildView(vgReferenceDetail);
        //TODO: Replace with form binding
        if(value!= null) {

        }
    }

    @Override
    public List<ReferenceService.Reference> getDataFromForm(List<ReferenceService.Reference> detail) {
        if(detail == null)
            detail = new ArrayList<>();
        return detail;
    }

}
