package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.FacebookFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class SocialPagerAdapter extends FragmentPagerAdapter{

    private final FragmentManager fragmentManager;
    private String tabtitles[] = new String[] { "Facebook"};
    private FacebookFragment facebookFragment;

    public SocialPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        facebookFragment = new FacebookFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return facebookFragment ;
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
