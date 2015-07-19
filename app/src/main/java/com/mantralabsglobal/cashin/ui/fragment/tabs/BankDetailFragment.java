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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BankDetailFragment extends BaseBindableFragment<List<PrimaryBankService.BankDetail>> {

    PrimaryBankService primaryBankService;

    @InjectView(R.id.vg_bank_details)
    ViewGroup vg_bank_details;

    List<BankDetailView> bankDetailViewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_bank_detail, container, false);
        return view;
    }

    @Override
    protected View getFormView() {
        return vg_bank_details;
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
        final SMSProvider provider = new SMSProvider(getActivity());
        List<SMSProvider.SMSMessage> smsList = provider.readSMS(new Predicate<SMSProvider.SMSMessage>() {
            @Override
            public boolean apply(SMSProvider.SMSMessage smsMessage) {
                if (provider.hasAccountInformation(smsMessage) && provider.isSenderBank(smsMessage)) {
                    return true;
                }
                return false;
            }
        });

        List<PrimaryBankService.BankDetail> bankDetailList = new ArrayList<>();
        final Map<String, Integer> bankCount = new HashMap<>();
        for(SMSProvider.SMSMessage smsMessage: smsList)
        {
            PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
            bankDetail.setAccountNumber(provider.getAccountNumber(smsMessage));
            bankDetail.setBankName(provider.getBankName(smsMessage));
            bankDetail.setIsPrimary(smsList.size() == 1);
            if(!bankDetailList.contains(bankDetail))
                bankDetailList.add(bankDetail);
            if(!bankCount.containsKey(bankDetail.getAccountNumberLast4Digits()))
                bankCount.put(bankDetail.getAccountNumberLast4Digits(), 0);
            int newCount = bankCount.get(bankDetail.getAccountNumberLast4Digits()) + 1;
            bankCount.put(bankDetail.getAccountNumberLast4Digits(), newCount);
        }


        if(bankDetailList.size()>0) {
            Collections.sort(bankDetailList, new Comparator<PrimaryBankService.BankDetail>() {

                @Override
                public int compare(PrimaryBankService.BankDetail lhs, PrimaryBankService.BankDetail rhs) {
                    return bankCount.get(rhs.getAccountNumberLast4Digits()).compareTo(bankCount.get(lhs.getAccountNumberLast4Digits()));
                }
            });
            bankDetailList.get(0).setIsPrimary(true);
         }

        bindDataToForm(bankDetailList);
    }

    @Override
    protected void onUpdate(List<PrimaryBankService.BankDetail> updatedData, Callback<List<PrimaryBankService.BankDetail>> saveCallback) {
        primaryBankService.createPrimaryBankDetail(updatedData, saveCallback);
    }

    @Override
    public void bindDataToForm(final List<PrimaryBankService.BankDetail> value) {
        if(value != null)
        {
            bankDetailViewList = new ArrayList<>();
            for(final PrimaryBankService.BankDetail bankDetail: value)
            {
                BankDetailView view = new BankDetailView(getActivity());
                view.setBankDetail(bankDetail);
                vg_bank_details.addView(view);
                bankDetailViewList.add(view);
                view.addPrimaryFlagChangeListener(new BankDetailView.PrimaryFlagChangedListener() {
                    @Override
                    public void onPrimaryChanged(BankDetailView bankDetailView) {
                        bankDetailView.getBankDetail().setIsPrimary(true);
                        bankDetailView.updateUI();
                        for(BankDetailView bdview : bankDetailViewList)
                        {
                            if(bdview != bankDetailView)
                            {
                                bdview.getBankDetail().setIsPrimary(false);
                            }
                            bdview.updateUI();
                        }
                    }
                });
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
