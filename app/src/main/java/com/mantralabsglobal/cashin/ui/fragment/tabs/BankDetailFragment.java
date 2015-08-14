package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.fragment.utils.TabManager;
import com.mantralabsglobal.cashin.ui.view.BankDetailView;
import com.mantralabsglobal.cashin.utils.SMSProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
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

    @InjectView(R.id.info_bar)
    ViewGroup vgInfoBar;
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
        mTabManager.addTab(mTabHost.newTabSpec("take_snap").setIndicator("Upload bank statement"),
                SnapBankStatementFragment.class, null);

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

    @OnClick(R.id.btn_add_more)
    public void addMoreInBankStatement(){
       /* if(bankDetailViewList.size() == 0 || (bankDetailViewList.get(bankDetailViewList.size()-1).getBankDetail().getAccountNumber() != null
                && bankDetailViewList.get(bankDetailViewList.size()-1).getBankDetail().getAccountNumber().trim().length() > 0)) {*/
            PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
            onCreateDialog(bankDetail);
      /*  }
     else {
            Toast.makeText(getActivity(), "Please enter account number in previous row!",Toast.LENGTH_LONG).show();
        }*/
    }

    public void onCreateDialog(final PrimaryBankService.BankDetail bankDetail) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Bank");
        builder.setItems(R.array.bank_code, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String bankName = Arrays.asList((getActivity().getResources().getStringArray(R.array.bank_code))).get(item);
                Log.d("Bank name", bankName);
                dialog.dismiss();
                bankDetail.setBankName(bankName);
                addMoreBankDetail(bankDetail);
            }
        });
        builder.show();
    }


    private void addMoreBankDetail(final PrimaryBankService.BankDetail bankDetail){
        final BankDetailView view = new BankDetailView(getActivity());
        /*Editable accountNo = view.accountNumber.getText();
        String accountNum = accountNo.toString();
        bankDetail.setAccountNumber(accountNum);*/
        view.setBankDetail(bankDetail);
        vg_bank_details.addView(view);
        addMoreAccNumberListener(view);
     /*   view.accountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bankDetail.setAccountNumber(s.toString());
                Log.d("Account Number", s.toString());
                view.setBankDetail(bankDetail);

            }
        }); */

    }


            @Override
            protected void handleDataNotPresentOnServer() {
                final SMSProvider provider = new SMSProvider(getActivity());
                List<SMSProvider.SMSMessage> smsList = provider.readSMS(new Predicate<SMSProvider.SMSMessage>() {
                    @Override
                    public boolean apply(SMSProvider.SMSMessage smsMessage) {
                        return provider.isSenderBank(smsMessage) && provider.hasAccountInformation(smsMessage);
                    }
                });

                List<PrimaryBankService.BankDetail> bankDetailList = new ArrayList<>();
                final Map<String, Integer> bankCount = new HashMap<>();
                for (SMSProvider.SMSMessage smsMessage : smsList) {
                    PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
                    bankDetail.setAccountNumber(provider.getAccountNumber(smsMessage));
                    bankDetail.setBankName(provider.getBankName(smsMessage));
                    // bankDetail.setIsPrimary(smsList.size() == 1);++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++6\
                    // ++++++++++++++++++++++                   if (!bankDetailList.contains(bankDetail))
                        bankDetailList.add(bankDetail);
                    if (!bankCount.containsKey(bankDetail.getAccountNumberLast4Digits()))
                        bankCount.put(bankDetail.getAccountNumberLast4Digits(), 0);
                    int newCount = bankCount.get(bankDetail.getAccountNumberLast4Digits()) + 1;
                    bankCount.put(bankDetail.getAccountNumberLast4Digits(), newCount);
                }
                sortBankDetailsList(bankDetailList, bankCount);
            }

            private void sortBankDetailsList(List<PrimaryBankService.BankDetail> bankDetailList, final Map<String, Integer> bankCount) {
                if (bankDetailList.size() > 0) {
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
                if (value != null) {
                    bankDetailViewList = new ArrayList<>();
                    for (final PrimaryBankService.BankDetail bankDetail : value) {
                        BankDetailView view = new BankDetailView(getActivity());
                        view.setBankDetail(bankDetail);
                        vg_bank_details.addView(view);
                        bankDetailViewList.add(view);
                        primaryBankChangeListener(view);

                    }
                }
            }


            private void primaryBankChangeListener(BankDetailView view) {
                view.addPrimaryFlagChangeListener(new BankDetailView.PrimaryFlagChangedListener() {
                    @Override
                    public void onPrimaryChanged(BankDetailView bankDetailView) {
                        bankDetailView.getBankDetail().setIsPrimary(true);
                        bankDetailView.updateUI();
                        handlePrimaryBankSelected(bankDetailView);
                    }
                });
            }


    private void addMoreAccNumberListener(final BankDetailView view) {
        view.addAddMoreAccountNumberListener(new BankDetailView.AddMoreAccountNumberListener(){
            @Override
            public void onAccountNumberChanged(BankDetailView bankDetailView) {
                if(!bankDetailViewList.contains(bankDetailView)) {
                    bankDetailViewList.add(bankDetailView);
                    primaryBankChangeListener(bankDetailView);
                }
            }
        });
    }

    private void handlePrimaryBankSelected(BankDetailView bankDetailView) {

        bank_detail_add_more.setVisibility(View.GONE);
        vgInfoBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        for(BankDetailView bdView : bankDetailViewList)
        {
            if(bdView != bankDetailView)
            {
                bdView.getBankDetail().setIsPrimary(false);
                vg_bank_details.removeView(bdView);
            }
            bdView.updateUI();
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
