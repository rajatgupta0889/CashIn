package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.GooglePlus;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.ui.fragment.adapter.IntroSliderFragmentAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class GetStartedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.inject(this);

    }

    @OnClick(R.id.get_started_button)
    public void launchMainActivity()
    {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
