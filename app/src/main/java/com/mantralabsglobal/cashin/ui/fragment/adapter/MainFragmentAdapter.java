package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;

import butterknife.OnEditorAction;

/**
 * Created by pk on 6/27/2015.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private Context context;

    private Fragment financePager = new AbstractPager(){

        FinancePagerAdapter adapter;
        @Override
        protected FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new FinancePagerAdapter(fragmentManager);
            return adapter;
        }

        @Override
        protected void setTabLayoutMode(FragmentPagerAdapter fragmentPagerAdapter, TabLayout tabLayout)
        {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
        @Override
        protected Context getContext()
        {
            return context;
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
        @Override
        protected Context getContext()
        {
            return context;
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
        @Override
        protected Context getContext()
        {
            return context;
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
        @Override
        protected Context getContext()
        {
            return context;
        }
    };

    private Fragment photoPager = new AbstractPager(){

        YourPhotoPagerAdapter adapter;
        @Override
        protected YourPhotoPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new YourPhotoPagerAdapter(fragmentManager);
            return adapter;
        }
        @Override
        protected Context getContext()
        {
            return context;
        }
    };

    public MainFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
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
