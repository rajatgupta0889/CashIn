package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BusinessCardFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.LinkedInFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class WorkPagerAdapter extends FragmentPagerAdapter{

    private final FragmentManager fragmentManager;
    private String tabtitles[] = new String[] { "Business Card", "Linked In"};
    private BusinessCardFragment businessCardFragment;
    private LinkedInFragment linkedInFragment;

    public WorkPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        businessCardFragment = new BusinessCardFragment();
        linkedInFragment = new LinkedInFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return businessCardFragment ;
            case 1:
                return linkedInFragment;
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
