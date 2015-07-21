package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.FacebookFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.ReferencesFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class SocialPagerAdapter extends FragmentPagerAdapter{

    private final FragmentManager fragmentManager;
    private String tabtitles[] = new String[] { "Facebook", "References"};
    private FacebookFragment facebookFragment;
    private ReferencesFragment referencesFragment;

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        facebookFragment = new FacebookFragment();
        referencesFragment = new ReferencesFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return facebookFragment ;
            case 1:
                return referencesFragment;
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
