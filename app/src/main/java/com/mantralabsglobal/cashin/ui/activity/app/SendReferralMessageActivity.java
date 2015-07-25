package com.mantralabsglobal.cashin.ui.activity.app;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.ReferenceService;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by hello on 7/25/2015.
 */
public class SendReferralMessageActivity extends BaseActivity {

    public static final String REFERRALS = "REFERRALS";
    private Toolbar toolbar;

    @InjectView(R.id.vg_referrals)
    FlowLayout vg_referrals;
    @InjectView(R.id.et_message_content)
    EditText etMessageContent;

    @InjectView(R.id.cb_default_message)
    CheckBox checkDefaultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        setTitle(R.string.send_message_to_referrals);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        ArrayList<ReferenceService.Reference> referenceArrayList = (ArrayList<ReferenceService.Reference> )getIntent().getSerializableExtra(REFERRALS);
        for(ReferenceService.Reference reference : referenceArrayList)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            View  view = layoutInflater.inflate(R.layout.contact_name_token_view, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_contact_name);
            tv.setText(reference.getName());
            vg_referrals.addView(view);
        }
        onDefaultContentCheckedChanged();
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnCheckedChanged(R.id.cb_default_message)
    public void onDefaultContentCheckedChanged()
    {
        if(checkDefaultMessage.isChecked())
        {
            resetMessageContent();
            etMessageContent.setFocusable(false);
            etMessageContent.setClickable(false);
        }
        else
        {
            etMessageContent.setFocusableInTouchMode(true);
            etMessageContent.setFocusable(true);
            etMessageContent.setClickable(true);
        }
    }

    public void resetMessageContent()
    {
        String messageContent = String.format(getString(R.string.default_referral_message_content_new), getCashInApplication().getAppUser());
        etMessageContent.setText(messageContent);
    }

    @OnClick(R.id.btn_send_message)
    public void onSendMessage()
    {
        showToastOnUIThread("Not implemented");
    }
}
