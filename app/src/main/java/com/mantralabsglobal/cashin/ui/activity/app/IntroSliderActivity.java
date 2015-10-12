package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.GooglePlus;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.ui.fragment.adapter.IntroSliderFragmentAdapter;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class IntroSliderActivity extends BaseActivity {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @InjectViews({R.id.view_one, R.id.view_two, R.id.view_three, R.id.view_four})
    List<ImageView> pageIndicators;

    GooglePlus googlePlus;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);


        ButterKnife.inject(this);
        googlePlus = new GooglePlus();

        final IntroSliderFragmentAdapter adapter = new IntroSliderFragmentAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public boolean callHappened;
            public boolean mPageEnd;
            public int selectedIndex;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*if( mPageEnd && position == adapter.getCount()-1 && !callHappened)
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
                }*/
            }

            @Override
            public void onPageSelected(int position) {
                int length = pageIndicators.size();
                LinearLayout one = (LinearLayout) findViewById(R.id.gplus_sign_in_button);
                if( position == length-1 )
                    one.setVisibility(View.VISIBLE);
                else
                    one.setVisibility(View.INVISIBLE);

                selectedIndex = position;

                for (int i = 0; i < length ; i++) {
                    if (i == position)
                        pageIndicators.get(i).setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_dot_selected));
                    else
                        pageIndicators.get(i).setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_dot));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*if(selectedIndex == adapter.getCount() - 1)
                {
                    mPageEnd = true;
                }*/
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.gplus_sign_in_button)
    public void signInWithGoogle() {
        showProgressDialog(getString(R.string.title_please_wait), getString(R.string.signing_in), true, false);
        googlePlus.authenticate(this , new SocialBase.SocialListener<String>() {
            @Override
            public void onSuccess(final String email) {
                IntroSliderActivity.this.email = email;
                tokenTask.execute(IntroSliderActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideProgressDialog();
                Snackbar.make(viewPager, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private GoogleTokenRetrieverTask tokenTask = new GoogleTokenRetrieverTask(){


        @Override
        protected String getEmail() {
            return IntroSliderActivity.this.email;
        }

        @Override
        public void onException(UserRecoverableAuthException e) {
            startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
        }

        @Override
        public void onException(IOException e) {
            super.onException(e);
            showToastOnUIThread(e.getMessage());
        }

        @Override
        public void onException(GoogleAuthException e) {
            super.onException(e);
            showToastOnUIThread(e.getMessage());
        }

        @Override
        protected void afterTokenRecieved(String email, String token) {
            registerAndLogin(email, token, true, new IAuthListener() {
                @Override
                public void onSuccess() {
                    hideProgressDialog();
                    Intent intent = new Intent(getBaseContext(), GetStartedActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception exp) {
                    hideProgressDialog();
                    Snackbar.make(viewPager, exp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_IN_REQUIRED && resultCode == RESULT_OK) {
            tokenTask.execute(this);
        }
        else {
            googlePlus.onActivityResult(requestCode, resultCode, data);
        }
    }

}
