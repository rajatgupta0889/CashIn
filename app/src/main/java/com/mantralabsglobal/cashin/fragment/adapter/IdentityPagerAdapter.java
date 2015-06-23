package com.mantralabsglobal.cashin.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.fragment.tabs.AadharCardFragment;
import com.mantralabsglobal.cashin.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.fragment.tabs.CurrentAddressFragment;
import com.mantralabsglobal.cashin.fragment.tabs.PANCardFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class IdentityPagerAdapter extends FragmentPagerAdapter{

    private String tabtitles[] = new String[] { "Aadhar", "PAN"};

    AadharCardFragment aadharCardFragment;
    PANCardFragment panCardFragment;

    public IdentityPagerAdapter(FragmentManager fm) {
        super(fm);
        aadharCardFragment = new AadharCardFragment();
        panCardFragment = new PANCardFragment();

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                 return aadharCardFragment ;
            case 1:
                return panCardFragment;
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
