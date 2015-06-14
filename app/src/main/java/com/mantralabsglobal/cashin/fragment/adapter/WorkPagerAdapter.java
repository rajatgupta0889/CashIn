package com.mantralabsglobal.cashin.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.fragment.tabs.BusinessCardFragment;
import com.mantralabsglobal.cashin.fragment.tabs.CurrentAddressFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class WorkPagerAdapter extends FragmentPagerAdapter{

    private String tabtitles[] = new String[] { "Business Card", "Linked In"};
    private BusinessCardFragment fragment;

    public WorkPagerAdapter(FragmentManager fm) {
        super(fm);
        fragment = new BusinessCardFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return fragment ;
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
