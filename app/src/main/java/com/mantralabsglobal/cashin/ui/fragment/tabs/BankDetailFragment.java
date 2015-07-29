package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.android.internal.util.Predicate;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.fragment.utils.TabManager;
import com.mantralabsglobal.cashin.ui.view.BankDetailView;
import com.mantralabsglobal.cashin.utils.SMSProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class BankDetailFragment extends BaseBindableFragment<List<PrimaryBankService.BankDetail>> {

PrimaryBankService primaryBankService;    private ViewPager mViewPager;

    TabManager mTabManager;
    LinearLayout mainLayout, bank_detail_add_more;

    @InjectView(R.id.vg_bank_details)
    ViewGroup vg_bank_details;
    private TabHost mTabHost;

    List<BankDetailView> bankDetailViewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabManager = new TabManager(getActivity(),getChildFragmentManager(),android.R.id.tabcontent);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        mainLayout = (LinearLayout)view.findViewById(R.id.bank_statement);

        bank_detail_add_more = (LinearLayout)view.findViewById(R.id.bank_detail);

        mTabHost  = mTabManager.handleCreateView(view);

        mTabManager.addTab(mTabHost.newTabSpec("blank").setIndicator("Blank Fragment"),
                BlankFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("e_statement").setIndicator("E-Statement"),
                EStatementFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("net_banking").setIndicator("Net Banking"),
                NetBankingFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("take_snap").setIndicator("Take a Snap"),
                TakeSnapFragment.class, null);

        mTabHost.setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);

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
                return provider.hasAccountInformation(smsMessage) && provider.isSenderBank(smsMessage);
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
        //    bankDetailList.get(0).setIsPrimary(true);
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
                        handlePrimaryBankSelected(bankDetailView);

                    }
                });
            }
        }
    }

    private void handlePrimaryBankSelected(BankDetailView bankDetailView) {

        bank_detail_add_more.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        for(BankDetailView bdview : bankDetailViewList)
        {
            if(bdview != bankDetailView)
            {
                bdview.getBankDetail().setIsPrimary(false);
                vg_bank_details.removeView(bdview);
            }
            bdview.updateUI();
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mTabManager.getCurrentFragment() != null)
            mTabManager.getCurrentFragment().onActivityResult(requestCode,resultCode,data);
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
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(mTabManager != null)
            mTabManager.handleViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabManager.handleDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTabManager.handleSaveInstanceState(outState);
    }

}
