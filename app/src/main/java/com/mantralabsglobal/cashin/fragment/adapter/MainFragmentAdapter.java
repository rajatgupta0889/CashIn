package com.mantralabsglobal.cashin.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.fragment.AbstractPager;
import com.mantralabsglobal.cashin.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.fragment.tabs.CurrentAddressFragment;
import com.mantralabsglobal.cashin.fragment.tabs.IncomeFragment;

/**
 * Created by pk on 6/27/2015.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;

    private Fragment financePager = new AbstractPager(){

        FinancePagerAdapter adapter;
        @Override
        protected FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new FinancePagerAdapter(fragmentManager);
            return adapter;
        }
    };

    private Fragment identityPager = new AbstractPager(){

        IdentityPagerAdapter adapter;
        @Override
        protected IdentityPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new IdentityPagerAdapter(fragmentManager);
            return adapter;
        }
    };

    private Fragment workPager = new AbstractPager(){

        WorkPagerAdapter adapter;
        @Override
        protected WorkPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new WorkPagerAdapter(fragmentManager);
            return adapter;
        }
    };

    private Fragment socialPager = new AbstractPager(){

        SocialPagerAdapter adapter;
        @Override
        protected SocialPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new SocialPagerAdapter(fragmentManager);
            return adapter;
        }
    };

    private Fragment photoPager = new AbstractPager(){

        SocialPagerAdapter adapter;
        @Override
        protected SocialPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new SocialPagerAdapter(fragmentManager);
            return adapter;
        }
    };

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return photoPager;
            case 1:
                return identityPager;
            case 2:
                return workPager ;
            case 3:
                return financePager ;
            case 4:
                return socialPager ;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
