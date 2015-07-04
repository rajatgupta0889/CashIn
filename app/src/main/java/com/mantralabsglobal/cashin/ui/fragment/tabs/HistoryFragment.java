package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.UserHistoryService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class HistoryFragment extends BaseBindableFragment<UserHistoryService.UserHistory> {

    @InjectView(R.id.switch_loan_taken)
    Switch loanTaken;
    @InjectView(R.id.switch_defaulted)
    Switch hasDefaulted;
    @InjectView(R.id.switch_cheque_bounced)
    Switch chequeBounced;
    @InjectView(R.id.switch_application_rejected)
    Switch applicationRejected;

    UserHistoryService userHistoryService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userHistoryService = ((Application)getActivity().getApplication()).getRestClient().getUserHistoryService();
        reset(false);
    }

    @Override
    protected void onUpdate(UserHistoryService.UserHistory updatedData, Callback<UserHistoryService.UserHistory> saveCallback) {
        userHistoryService.updateUserHistory(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(UserHistoryService.UserHistory updatedData, Callback<UserHistoryService.UserHistory> saveCallback) {
        userHistoryService.createUserHistory(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<UserHistoryService.UserHistory> dataCallback) {
        userHistoryService.getUserHistory(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        //Do Nothing
    }


    @Override
    public void bindDataToForm(UserHistoryService.UserHistory value) {
        if(value != null)
        {
            hasDefaulted.setChecked(value.isHasDefaulted());
            loanTaken.setChecked(value.isLoanTaken());
            chequeBounced.setChecked(value.isChequeBounced());
            applicationRejected.setChecked(value.isApplicationRejected());
        }
    }

    @Override
    public UserHistoryService.UserHistory getDataFromForm(UserHistoryService.UserHistory base) {

        if(base == null)
        {
            base = new UserHistoryService.UserHistory();
        }

        base.setApplicationRejected(applicationRejected.isChecked());
        base.setHasDefaulted(hasDefaulted.isChecked());
        base.setIsChequeBounced(chequeBounced.isChecked());
        base.setLoanTaken(loanTaken.isChecked());

        return base;
    }

    @Override
    public boolean isFormValid()
    {
        return true;
    }
}
