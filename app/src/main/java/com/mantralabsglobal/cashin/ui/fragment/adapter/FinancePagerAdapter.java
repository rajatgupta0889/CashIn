package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.CurrentAddressFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.HistoryFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.IncomeFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.PermanentAddressFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class FinancePagerAdapter extends FragmentPagerAdapter{

    private FragmentManager fragmentManager;
    private String tabtitles[] = new String[] {"Bank", "Credit Card",  "Bank Statement", "Income", "Current Address" , "Permanent Address", "EMIs", "History"};

    private IncomeFragment ifragnent;
    private BankDetailFragment bankDetailFragment;
    private CurrentAddressFragment currentAddressFragment;
    private PermanentAddressFragment permanentAddressFragment;
    private HistoryFragment userHistoryFragment;


    public FinancePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        ifragnent = new IncomeFragment();
        bankDetailFragment = new BankDetailFragment();
        currentAddressFragment = new CurrentAddressFragment();
        permanentAddressFragment = new PermanentAddressFragment();
        userHistoryFragment = new HistoryFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return bankDetailFragment;
            case 3:
                return ifragnent ;
            case 4:
                return currentAddressFragment ;
            case 5:
                return permanentAddressFragment ;
            case 7:
                return userHistoryFragment ;
            // Open FragmentTab3.java
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
