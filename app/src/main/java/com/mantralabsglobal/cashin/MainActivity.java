package com.mantralabsglobal.cashin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.mantralabsglobal.cashin.fragment.AbstractPager;
import com.mantralabsglobal.cashin.fragment.adapter.FinancePagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.IdentityPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.SocialPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.WorkPagerAdapter;


public class MainActivity extends FragmentActivity{



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
                        return new FinancePagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.yourIdentityButton))
                        return new IdentityPagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.workButton))
                        return new WorkPagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.financialButton))
                        return new FinancePagerAdapter(fragmentManager);
                    if (v == findViewById(R.id.socialButton))
                        return new SocialPagerAdapter(fragmentManager);
                    return null;
                }
            };

            if(f_pager != null) {
                FT.replace(R.id.main_frame, f_pager);
                FT.commit();
            }
        }
    };

}
