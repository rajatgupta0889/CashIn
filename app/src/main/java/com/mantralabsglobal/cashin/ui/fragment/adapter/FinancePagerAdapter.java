package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.CurrentAddressFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.EMIFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.HistoryFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.IncomeFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.PermanentAddressFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class FinancePagerAdapter extends FragmentPagerAdapter{

    private FragmentManager fragmentManager;
    private String tabtitles[] = new String[] {"Bank",  "Income", "EMI", "History"};

    private IncomeFragment ifragnent;

    private BankDetailFragment bankDetailFragment;

    private EMIFragment emiFragment;

    private HistoryFragment userHistoryFragment;


    public FinancePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        ifragnent = new IncomeFragment();
        bankDetailFragment = new BankDetailFragment();
        userHistoryFragment = new HistoryFragment();
        emiFragment = new EMIFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return bankDetailFragment;
            case 1:
                return ifragnent ;
            case 2:
                return emiFragment ;
            case 3:
                return userHistoryFragment ;
            // Open FragmentTab4.java
        }
        return new BlankFragment();
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

}
