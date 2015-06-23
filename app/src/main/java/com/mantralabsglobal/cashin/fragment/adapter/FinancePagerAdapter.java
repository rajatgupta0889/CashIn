package com.mantralabsglobal.cashin.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.fragment.tabs.CurrentAddressFragment;
import com.mantralabsglobal.cashin.fragment.tabs.IncomeFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class FinancePagerAdapter extends FragmentPagerAdapter{

    private String tabtitles[] = new String[] {"Bank", "Credit Card",  "Bank Statement", "Income", "Current Address" , "Permanent Address", "Expenses", "History"};

    private IncomeFragment ifragnent;
    private BankDetailFragment bankDetailFragment;
    private CurrentAddressFragment currentAddressFragment;

    public FinancePagerAdapter(FragmentManager fm) {
        super(fm);
        ifragnent = new IncomeFragment();
        bankDetailFragment = new BankDetailFragment();
        currentAddressFragment = new CurrentAddressFragment();
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
}
