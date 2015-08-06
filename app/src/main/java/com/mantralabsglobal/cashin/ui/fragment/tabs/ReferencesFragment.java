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
import android.widget.ExpandableListView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.ContactResult;
import com.mantralabsglobal.cashin.service.ReferenceService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.ContactPickerActivity;
import com.mantralabsglobal.cashin.ui.activity.app.SendReferralMessageActivity;
import com.mantralabsglobal.cashin.ui.fragment.adapter.ReferenceListAdapter;

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
    ReferenceListAdapter referenceListAdapter;

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
        referenceService.updateReferences(updatedData, saveCallback);
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


    @OnClick( {R.id.fab_select_reference, R.id.fab_edit_reference})
    public void loadReferences() {
        Intent intent = new Intent(getActivity(), ContactPickerActivity.class);
        intent.putExtra(ContactPickerActivity.PICKER_TYPE, ContactPickerActivity.PICKER_TYPE_PHONE);
        getActivity().startActivityForResult(intent, BaseActivity.CONTACT_PICKER);
        /*startActivityForResult(new Intent(getActivity(), ContactsPickerActivity.class), BaseActivity.CONTACT_PICKER);*/
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

        if(requestCode == BaseActivity.CONTACT_PICKER && resultCode == Activity.RESULT_OK)
        {
            ArrayList<ContactResult> resultList =  (ArrayList<ContactResult> )data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT);
            List<ReferenceService.Reference> referenceList = new ArrayList<>();
            for(ContactResult cr: resultList)
            {
                ReferenceService.Reference reference = new ReferenceService.Reference();
                reference.setName(cr.getContactName());
                reference.setNumber(cr.getResults().get(0).getResult());
                referenceList.add(reference);
            }
            if(referenceList.size()>0)
                bindDataToForm(referenceList);
        }
        else if(requestCode == BaseActivity.SEND_REFERRAL_MESSAGE && resultCode == Activity.RESULT_OK)
        {
            //Do something
        }

    }

    @OnClick(R.id.btn_send_request)
    public void onSendMessageClick()
    {
        Intent intent = new Intent(getActivity(), SendReferralMessageActivity.class);
        intent.putExtra(SendReferralMessageActivity.REFERRALS, new ArrayList<>(referenceListAdapter.getReferenceList()));
        getActivity().startActivityForResult(intent, BaseActivity.SEND_REFERRAL_MESSAGE);
    }

    @Override
    public void bindDataToForm(List<ReferenceService.Reference> value) {
        setVisibleChildView(vgReferenceDetail);
        if(value!= null) {
            ExpandableListView expandableListView = (ExpandableListView) getCurrentView().findViewById(R.id.elv_reference_list);
            expandableListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            if(referenceListAdapter == null)
                referenceListAdapter= new ReferenceListAdapter(getActivity(), value);
            else
                referenceListAdapter.setReferenceData(value);
            expandableListView.setAdapter(referenceListAdapter);
        }
    }

    @Override
    public List<ReferenceService.Reference> getDataFromForm(List<ReferenceService.Reference> detail) {
        if(detail == null)
            detail = new ArrayList<>();
        if(referenceListAdapter != null)
        {
            detail.clear();
            detail.addAll(referenceListAdapter.getReferenceList());
        }
        return detail;
    }

}
