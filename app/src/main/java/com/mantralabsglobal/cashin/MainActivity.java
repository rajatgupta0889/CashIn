package com.mantralabsglobal.cashin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.mantralabsglobal.cashin.fragment.AbstractPager;
import com.mantralabsglobal.cashin.fragment.adapter.FinancePagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.IdentityPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.SocialPagerAdapter;
import com.mantralabsglobal.cashin.fragment.adapter.WorkPagerAdapter;
import com.sromku.simple.fb.SimpleFacebook;


public class MainActivity extends FragmentActivity{

    private FinancePagerAdapter financePagerAdapter;
    private IdentityPagerAdapter identityPagerAdapter;
    private WorkPagerAdapter workPagerAdapter;
    private SocialPagerAdapter socialPagerAdapter;
    private Button yourIdentityButton;
    private Button yourPhotoButton;
    private Button workButton;
    private Button financialButton;
    private Button socialButton;
    private SimpleFacebook mSimpleFacebook;
    Fragment f_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yourIdentityButton = (Button)findViewById(R.id.yourIdentityButton);
        yourIdentityButton.setOnClickListener(clickListener);

        yourPhotoButton = (Button)findViewById(R.id.yourPhotoButton);
        yourPhotoButton.setOnClickListener(clickListener);

        workButton = (Button)findViewById(R.id.workButton);
        workButton.setOnClickListener(clickListener);

        financialButton = (Button)findViewById(R.id.financialButton);
        financialButton.setOnClickListener(clickListener);

        socialButton = (Button)findViewById(R.id.socialButton);
        socialButton.setOnClickListener(clickListener);

        //ViewPager financialviewPager = (ViewPager) findViewById(R.id.financial);
        //financialviewPager.setAdapter(new FinancePagerAdapter(getSupportFragmentManager()));
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            final android.support.v4.app.FragmentManager FM = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction FT = FM.beginTransaction();

            f_pager = new AbstractPager() {
                @Override
                protected FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
                    if (v == yourPhotoButton)
                        return getFinancePageAdapter(fragmentManager);
                    if (v == yourIdentityButton)
                        return getIdentityPagerAdapter(fragmentManager);
                    if (v == workButton)
                        return getWorkPagerAdapter(fragmentManager);
                    if (v == financialButton)
                        return getFinancePageAdapter(fragmentManager);
                    if (v == socialButton)
                        return getSocialPageAdapter(fragmentManager);
                    return null;
                }
            };

            if(f_pager != null) {
                FT.replace(R.id.main_frame, f_pager, "");
                //FT.addToBackStack(null);
                FT.commit();
            }

            //Update the button icon
            if (v == yourPhotoButton)
                yourPhotoButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_photoselected,0,0);
            else
                yourPhotoButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_photo,0,0);

            if (v == yourIdentityButton)
                yourIdentityButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_identityselected,0,0);
            else
                yourIdentityButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_identity,0,0);

            if (v == workButton)
                workButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_workselected,0,0);
            else
                workButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_work,0,0);

            if (v == financialButton)
                financialButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_financialselected,0,0);
            else
                financialButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_financial,0,0);

            if (v == socialButton)
                socialButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_socialselected,0,0);
            else
                socialButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_social,0,0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        f_pager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Application")
                .setMessage(R.string.confirm_on_close)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
