package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BankDetailFragment extends BaseBindableFragment<PrimaryBankService.BankDetail> {


    @NotEmpty
    @InjectView(R.id.cet_bank_name)
    public CustomEditText bankName;

    @NotEmpty
    @Digits
    @InjectView(R.id.cet_account_number)
    public CustomEditText accountNumber;

    PrimaryBankService primaryBankService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_bank_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        primaryBankService = ((Application)getActivity().getApplication()).getRestClient().getPrimaryBankService();
        reset(false);
    }

    @Override
    protected void onCreate(PrimaryBankService.BankDetail updatedData, Callback<PrimaryBankService.BankDetail> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<PrimaryBankService.BankDetail> dataCallback) {
        primaryBankService.getPrimaryBankDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    protected void onUpdate(PrimaryBankService.BankDetail updatedData, Callback<PrimaryBankService.BankDetail> saveCallback) {
        primaryBankService.updatePrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    public void bindDataToForm(PrimaryBankService.BankDetail value) {
        if(value != null)
        {
            bankName.setText(value.getBankName());
            accountNumber.setText(value.getAccountNumber());
        }
    }

    @Override
    public PrimaryBankService.BankDetail getDataFromForm(PrimaryBankService.BankDetail base) {
        if(base == null)
        {
            base = new PrimaryBankService.BankDetail();
        }
        base.setBankName(bankName.getText().toString());
        base.setAccountNumber(accountNumber.getText().toString());
        return base;
    }



}
