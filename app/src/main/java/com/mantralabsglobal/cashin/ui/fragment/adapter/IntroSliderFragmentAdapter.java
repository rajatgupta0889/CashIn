package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.intro.DocumentPhotosFragment;
import com.mantralabsglobal.cashin.ui.fragment.intro.MoneyCardFragment;
import com.mantralabsglobal.cashin.ui.fragment.intro.PreApprovalFragment;
import com.mantralabsglobal.cashin.ui.fragment.intro.SelfieFragment;

/**
 * Created by pk on 6/27/2015.
 */
public class IntroSliderFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;

    public IntroSliderFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SelfieFragment();
            case 1:
                return new DocumentPhotosFragment();
            case 2:
                return new PreApprovalFragment();
            case 3:
                return new MoneyCardFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
