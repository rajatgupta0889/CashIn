package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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

    @InjectView(R.id.rg_loan_taken)
    RadioGroup loanTaken;
    @InjectView(R.id.rg_defaulted)
    RadioGroup hasDefaulted;
    @InjectView(R.id.rg_cheque_bounced)
    RadioGroup chequeBounced;
    @InjectView(R.id.rg_application_rejected)
    RadioGroup applicationRejected;

    @InjectView(R.id.vg_form)
    ViewGroup vg_form;

    UserHistoryService userHistoryService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    protected View getFormView() {
        return vg_form;
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
            hasDefaulted.check(value.isHasDefaulted() ? R.id.rg_defaulted_yes : R.id.rg_defaulted_no);
            loanTaken.check(value.isLoanTaken() ? R.id.rg_loan_taken_yes : R.id.rg_loan_taken_no);
            chequeBounced.check(value.isChequeBounced() ? R.id.rg_cheque_bounced_yes : R.id.rg_cheque_bounced_no);
            applicationRejected.check(value.isApplicationRejected() ? R.id.rg_application_rejected_yes : R.id.rg_application_rejected_no);

        }
    }

    @Override
    public UserHistoryService.UserHistory getDataFromForm(UserHistoryService.UserHistory base) {

        if(base == null)
        {
            base = new UserHistoryService.UserHistory();
        }

        base.setApplicationRejected(applicationRejected.getCheckedRadioButtonId()==R.id.rg_application_rejected_yes);
        base.setHasDefaulted(hasDefaulted.getCheckedRadioButtonId()==R.id.rg_defaulted_yes);
        base.setIsChequeBounced(chequeBounced.getCheckedRadioButtonId()==R.id.rg_cheque_bounced_yes);
        base.setLoanTaken(loanTaken.getCheckedRadioButtonId()==R.id.rg_loan_taken_yes);

        return base;
    }

    @Override
    public boolean isFormValid()
    {
        return true;
    }
}
