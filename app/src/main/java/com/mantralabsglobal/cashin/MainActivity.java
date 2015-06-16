package com.mantralabsglobal.cashin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.mantralabsglobal.cashin.fragment.AbstractPager;
import com.mantralabsglobal.cashin.fragment.adapter.FinancePagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.IdentityPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.SocialPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.WorkPagerAdapter;


public class MainActivity extends FragmentActivity{

    private FinancePagerAdapter financePagerAdapter;
    private IdentityPagerAdapter identityPagerAdapter;
    private WorkPagerAdapter workPagerAdapter;
    private SocialPagerAdapter socialPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton yourIdentityButton = (ImageButton)findViewById(R.id.yourIdentityButton);
        yourIdentityButton.setOnClickListener(clickListener);

        ImageButton yourPhotoButton = (ImageButton)findViewById(R.id.yourPhotoButton);
        yourPhotoButton.setOnClickListener(clickListener);

        ImageButton workButton = (ImageButton)findViewById(R.id.workButton);
        workButton.setOnClickListener(clickListener);

        ImageButton financialButton = (ImageButton)findViewById(R.id.financialButton);
        financialButton.setOnClickListener(clickListener);

        ImageButton socialButton = (ImageButton)findViewById(R.id.socialButton);
        socialButton.setOnClickListener(clickListener);


        //ViewPager financialviewPager = (ViewPager) findViewById(R.id.financial);
        //financialviewPager.setAdapter(new FinancePagerAdapter(getSupportFragmentManager()));
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            final android.support.v4.app.FragmentManager FM = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction FT = FM.beginTransaction();

            Fragment f_pager = null;

            final FrameLayout fl = (FrameLayout )findViewById(R.id.main_frame);
            f_pager = new AbstractPager() {
                @Override
                protected FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
                    if (v == findViewById(R.id.yourPhotoButton))
                        return getFinancePageAdapter(fragmentManager);
                    if (v == findViewById(R.id.yourIdentityButton))
                        return getIdentityPagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.workButton))
                        return getWorkPagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.financialButton))
                        return getFinancePageAdapter(fragmentManager);
                    if (v == findViewById(R.id.socialButton))
                        return getSocialPageAdapter(fragmentManager);
                    return null;
                }
            };

            if(f_pager != null) {
                FT.replace(R.id.main_frame, f_pager);
                FT.addToBackStack(null);
                FT.commit();
            }
        }
    };

    protected FinancePagerAdapter getFinancePageAdapter(FragmentManager fragmentManager)
    {
        financePagerAdapter = new FinancePagerAdapter(fragmentManager);
        return financePagerAdapter;
    }

    protected SocialPagerAdapter getSocialPageAdapter(FragmentManager fragmentManager)
    {
        socialPagerAdapter = new SocialPagerAdapter(fragmentManager);
        return socialPagerAdapter;
    }

    protected WorkPagerAdapter getWorkPagerAdapter(FragmentManager fragmentManager)
    {
        workPagerAdapter = new WorkPagerAdapter(fragmentManager);
        return workPagerAdapter;
    }
    protected IdentityPagerAdapter getIdentityPagerAdapter(FragmentManager fragmentManager)
    {
        identityPagerAdapter = new IdentityPagerAdapter(fragmentManager);
        return identityPagerAdapter;
    }

}
