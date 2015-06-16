package com.mantralabsglobal.cashin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.fragment.adapter.FinancePagerAdapter;

/**
 * Created by pk on 14/06/2015.
 */
public abstract class AbstractPager extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        pager.setAdapter(getPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //tabLayout.setFillViewport(true);
    }

    protected abstract FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager);

}
