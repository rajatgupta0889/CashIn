package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

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
        TabHost host = mTabManager.handleCreateView(view);

        mainLayout = (LinearLayout)view.findViewById(R.id.bank_statement);

        bank_detail_add_more = (LinearLayout)view.findViewById(R.id.bank_detail);
        mTabManager.addTab(host.newTabSpec("blank").setIndicator("Blank Fragment"),
                BlankFragment.class, null);
        mTabManager.addTab(host.newTabSpec("e_statement").setIndicator("E-Statement"),
                EStatementFragment.class, null);
        mTabManager.addTab(host.newTabSpec("net_banking").setIndicator("Net Banking"),
                NetBankingFragment.class, null);
        mTabManager.addTab(host.newTabSpec("take_snap").setIndicator("Take a Snap"),
                TakeSnapFragment.class, null);

        host.setCurrentTab(0);
        host.getTabWidget().getChildAt(0).setVisibility(View.GONE);

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
                        bank_detail_add_more.setVisibility(View.INVISIBLE);
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
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
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

    static class TabManager implements TabHost.OnTabChangeListener {
        private final Context mContext;
        private final FragmentManager mManager;
        private final int mContainerId;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private TabHost mTabHost;
        private TabInfo mLastTab;
        private boolean mInitialized;
        private String mCurrentTabTag;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(Context context, FragmentManager manager, int containerId) {
            mContext = context;
            mManager = manager;
            mContainerId = containerId;
        }

        public TabHost handleCreateView(View root) {
            if (mTabHost != null) {
                throw new IllegalStateException("TabHost already set");
            }
            mTabHost = (TabHost)root.findViewById(android.R.id.tabhost);
            mTabHost.setup();
            mTabHost.setOnTabChangedListener(this);
            return mTabHost;
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();
            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
        }

        public void handleViewStateRestored(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                mCurrentTabTag = savedInstanceState.getString("tab");
            }
            mTabHost.setCurrentTabByTag(mCurrentTabTag);

            String currentTab = mTabHost.getCurrentTabTag();

            // Go through all tabs and make sure their fragments match
            // the correct state.
            FragmentTransaction ft = null;
            for (int i=0; i<mTabs.size(); i++) {
                TabInfo tab = mTabs.get(i);
                tab.fragment = mManager.findFragmentByTag(tab.tag);
                if (tab.fragment != null && !tab.fragment.isDetached()) {
                    if (tab.tag.equals(currentTab)) {
                        // The fragment for this tab is already there and
                        // active, and it is what we really want to have
                        // as the current tab.  Nothing to do.
                        mLastTab = tab;
                    } else {
                        // This fragment was restored in the active state,
                        // but is not the current tab.  Deactivate it.
                        if (ft == null) {
                            ft = mManager.beginTransaction();
                        }
                        ft.detach(tab.fragment);
                    }
                }
            }

            // We are now ready to go.  Make sure we are switched to the
            // correct tab.
            mInitialized = true;
            ft = doTabChanged(currentTab, ft);
            if (ft != null) {
                ft.commit();
                mManager.executePendingTransactions();
            }
        }

        public void handleDestroyView() {
            mCurrentTabTag = mTabHost.getCurrentTabTag();
            mTabHost = null;
            mTabs.clear();
            mInitialized = false;
        }

        public void handleSaveInstanceState(Bundle outState) {
            outState.putString("tab", mTabHost != null
                    ? mTabHost.getCurrentTabTag() : mCurrentTabTag);
        }

        @Override
        public void onTabChanged(String tabId) {
            if (!mInitialized) {
                return;
            }
            FragmentTransaction ft = doTabChanged(tabId, null);
            if (ft != null) {
                ft.commit();
            }
        }

        private FragmentTransaction doTabChanged(String tabId, FragmentTransaction ft) {
            TabInfo newTab = null;
            for (int i=0; i<mTabs.size(); i++) {
                TabInfo tab = mTabs.get(i);
                if (tab.tag.equals(tabId)) {
                    newTab = tab;
                }
            }
            if (newTab == null) {
                throw new IllegalStateException("No tab known for tag " + tabId);
            }
            if (mLastTab != newTab) {
                if (ft == null) {
                    ft = mManager.beginTransaction();
                }
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mContext,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
            }
            return ft;
        }
    }

}
