package com.mantralabsglobal.cashin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
        FragmentPagerAdapter fragmentPagerAdapter = getPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        if(fragmentPagerAdapter.getCount()<3)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return view;
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

}
