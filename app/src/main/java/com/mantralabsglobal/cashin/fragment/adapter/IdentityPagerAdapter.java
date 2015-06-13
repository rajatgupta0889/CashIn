package com.mantralabsglobal.cashin.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.fragment.tabs.CurrentAddressFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class IdentityPagerAdapter extends FragmentPagerAdapter{

    private String tabtitles[] = new String[] { "Aadhar", "PAN"};

    public IdentityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                CurrentAddressFragment currentAddressFragment = new CurrentAddressFragment();
                return currentAddressFragment ;
            // Open FragmentTab3.java
        }
        return new CurrentAddressFragment();
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
