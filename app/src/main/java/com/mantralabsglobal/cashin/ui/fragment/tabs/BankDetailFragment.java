package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.internal.util.Predicate;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.BankDetailView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.SMSProvider;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BankDetailFragment extends BaseBindableFragment<List<PrimaryBankService.BankDetail>> {

    PrimaryBankService primaryBankService;

    @InjectView(R.id.vg_bank_details)
    ViewGroup vg_bank_details;

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
    protected void onCreate(List<PrimaryBankService.BankDetail> updatedData, Callback<List<PrimaryBankService.BankDetail>> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<List<PrimaryBankService.BankDetail>> dataCallback) {
        primaryBankService.getPrimaryBankDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        final SMSProvider provider = new SMSProvider();
        List<SMSProvider.SMSMessage> smsList = provider.readSMS(getActivity(), new Predicate<SMSProvider.SMSMessage>() {
            @Override
            public boolean apply(SMSProvider.SMSMessage smsMessage) {
                if (provider.hasAccountInformation(smsMessage) && provider.isSenderBank(smsMessage)) {
                    return true;
                }
                return false;
            }
        });

        List<PrimaryBankService.BankDetail> bankDetailList = new ArrayList<>();
        for(SMSProvider.SMSMessage smsMessage: smsList)
        {
            PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
            bankDetail.setAccountNumber(provider.getAccountNumber(smsMessage));
            bankDetail.setBankName(provider.getBankName(smsMessage));
            bankDetail.setIsPrimary(smsList.size() == 1);
            if(!bankDetailList.contains(bankDetail))
                bankDetailList.add(bankDetail);
        }

        if(bankDetailList.size()==1)
            bankDetailList.get(0).setIsPrimary(true);

        bindDataToForm(bankDetailList);
    }

    @Override
    protected void onUpdate(List<PrimaryBankService.BankDetail> updatedData, Callback<List<PrimaryBankService.BankDetail>> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    public void bindDataToForm(List<PrimaryBankService.BankDetail> value) {
        if(value != null)
        {
            for(PrimaryBankService.BankDetail bankDetail: value)
            {
                BankDetailView view = new BankDetailView(getActivity());
                view.setBankDetail(bankDetail);
                vg_bank_details.addView(view);
            }
        }
    }

    @Override
    public List<PrimaryBankService.BankDetail> getDataFromForm(List<PrimaryBankService.BankDetail> base) {
      /*  if(base == null)
        {
            base = new PrimaryBankService.BankDetail();
        }
        base.setBankName(bankName.getText().toString());
        base.setAccountNumber(accountNumber.getText().toString());*/
        return base;
    }



}
