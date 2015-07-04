package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.fragment.adapter.IntroSliderFragmentAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class IntroSliderActivity extends AppCompatActivity {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @InjectViews({R.id.view_one, R.id.view_two, R.id.view_three, R.id.view_four})
    List<ImageView> pageIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        ButterKnife.inject(this);
        final IntroSliderFragmentAdapter adapter = new IntroSliderFragmentAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public boolean callHappened;
            public boolean mPageEnd;
            public int selectedIndex;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if( mPageEnd && position == adapter.getCount()-1 && !callHappened)
                {
                    Log.d(getClass().getName(), "Okay");
                    mPageEnd = false;//To avoid multiple calls.
                    callHappened = true;
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    mPageEnd = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                selectedIndex = position;
                for (int i = 0; i < pageIndicators.size(); i++) {
                    if (i == position)
                        pageIndicators.get(i).setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_dot_selected));
                    else
                        pageIndicators.get(i).setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_dot));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(selectedIndex == adapter.getCount() - 1)
                {
                    mPageEnd = true;
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

}
