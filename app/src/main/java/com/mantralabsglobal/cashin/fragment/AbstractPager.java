package com.mantralabsglobal.cashin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.fragment.adapter.FinancePagerAdapter;

import butterknife.ButterKnife;

/**
 * Created by pk on 14/06/2015.
 */
public abstract class AbstractPager extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager, container, false);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        FragmentPagerAdapter fragmentPagerAdapter = getPagerAdapter(getChildFragmentManager());
        pager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
        if(fragmentPagerAdapter.getCount()<3)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabLayout.setFillViewport(true);
    }

    protected abstract FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentPagerAdapter adapter = getPagerAdapter(getChildFragmentManager());
        for(int i=0;i<adapter.getCount();i++)
        {
            Fragment fragment = adapter.getItem(i);
            if(!fragment.isDetached())
                fragment.onActivityResult(requestCode,resultCode,data);
        }
    }

}
