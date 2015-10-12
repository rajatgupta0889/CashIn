package com.mantralabsglobal.cashin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.mantralabsglobal.cashin.R;

import butterknife.ButterKnife;

/**
 * Created by pk on 14/06/2015.
 */
public abstract class AbstractPager extends Fragment {

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager, container, false);
        ButterKnife.inject(this, view);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        final FragmentPagerAdapter fragmentPagerAdapter = getPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        setTabLayoutMode(fragmentPagerAdapter, tabLayout);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);

            }
        }, 250);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE && getContext() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                    }
                }
            }
        });

        return view;
    }

    protected void setTabLayoutMode(FragmentPagerAdapter fragmentPagerAdapter, TabLayout tabLayout)
    {
        if(fragmentPagerAdapter.getCount()<3) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
        else
        {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        //tabLayout.setFillViewport(true);
    }

    protected abstract FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AbstractPager", "onActivityResult" + this);

        FragmentPagerAdapter adapter = (FragmentPagerAdapter)viewPager.getAdapter();
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        fragment.onActivityResult(requestCode, resultCode, data);
        Log.d("AbstractPager", "onActivityResult invoked on " + fragment);
    }

    public boolean nextTab(){
        int currentIndex = viewPager.getCurrentItem();
        if(currentIndex < viewPager.getAdapter().getCount()-1)
        {
            viewPager.setCurrentItem(currentIndex+1, true);
            return true;
        }
        return false;
    }

    public boolean previousTab(){
        int currentIndex = viewPager.getCurrentItem();
        if(currentIndex > 0)
        {
            viewPager.setCurrentItem(currentIndex-1, true);
            return true;
        }
        return false;
    }

    public boolean moveToFirstTab(){
        int currentIndex = viewPager.getCurrentItem();
        if(currentIndex != 0)
        {
            viewPager.setCurrentItem(0, true);
            return true;
        }
        return false;
    }

    public boolean moveToLastTab(){
        int currentIndex = viewPager.getCurrentItem();
        if(currentIndex <= viewPager.getAdapter().getCount()-1)
        {
            viewPager.setCurrentItem(viewPager.getAdapter().getCount()-1, true);
            return true;
        }
        return false;
    }

    protected abstract Context getContext();
}
