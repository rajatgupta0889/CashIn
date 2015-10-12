package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.FacebookService;
import com.mantralabsglobal.cashin.social.Facebook;
import com.mantralabsglobal.cashin.social.SocialFactory;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.social.OAuthActivity;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.scribe.model.Token;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class FacebookFragment extends BaseBindableFragment<FacebookService.FacebookProfile>  {

    private EditText dobEditText;

    @NotEmpty
    @InjectView(R.id.et_connectedAs)
    public EditText connectedAs;

    @NotEmpty
    @InjectView(R.id.cet_workplace)
    public CustomEditText workplace;

    @NotEmpty
    @InjectView(R.id.cet_city)
    public CustomEditText city;

    @NotEmpty
    @InjectView(R.id.cet_hometown)
    public CustomEditText hometown;

    @NotEmpty
    @InjectView(R.id.cet_relationshipStatus)
    public CustomEditText relationshipStatus;

    @NotEmpty
    @InjectView(R.id.cet_dob)
    public BirthDayView dob;

    @InjectView(R.id.rl_facebook_details)
    public ViewGroup vg_facebookForm;

    @InjectView(R.id.ll_facebook_connect)
    public ViewGroup vg_facebookConnect;


    @InjectView(R.id.btn_facebook_connect)
    public Button loginButton;

    FacebookService facebookService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        facebookService = ((Application) getActivity().getApplication()).getRestClient().getFacebookService();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnFacebookConnect = (Button) view.findViewById(R.id.btn_facebook_connect);


        //List<String> permissions = Arrays.asList("public_profile","user_birthday", "user_hometown", "user_location", "user_relationship_details", "user_work_history");

        registerChildView(vg_facebookConnect, View.VISIBLE);
        registerChildView(vg_facebookForm, View.GONE);
        reset(false);



    }

    @Override
    protected View getFormView() {
        return vg_facebookForm;
    }

    @OnClick(R.id.btn_facebook_connect)
    public void onConnectClick() {

        Intent intent = new Intent(getActivity(), OAuthActivity.class);
        intent.putExtra("SOCIAL_NAME", SocialFactory.FACEBOOK);
        getActivity().startActivityForResult(intent, BaseActivity.FACEBOOK_SIGNIN);

        //new AsyncLinkedInProfileTask().execute();
    }

    @Override
    protected void onUpdate(FacebookService.FacebookProfile updatedData, Callback<FacebookService.FacebookProfile> saveCallback) {
        facebookService.updateFacebokProfile(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(FacebookService.FacebookProfile updatedData, Callback<FacebookService.FacebookProfile> saveCallback) {
        facebookService.createFacebokProfile(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<FacebookService.FacebookProfile> dataCallback) {
        facebookService.getFacebookProfile(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        String accessToken = ((Application)getActivity().getApplication()).getAppPreference().getString("facebook_access_token", null);
        String accessSecret = ((Application)getActivity().getApplication()).getAppPreference().getString("facebook_access_secret", null);
        if(accessToken!= null && accessSecret != null)
        {
            new AsyncFacebookProfileTask ().execute(accessToken, accessSecret);
        }
        else {
            showFacebookConnect();
        }

    }

    private class AsyncFacebookProfileTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                Facebook facebook = SocialFactory.getSocialHelper(SocialFactory.FACEBOOK, Facebook.class);
                Token token = facebook.getAccessToken(params[0], params[1]);
                final FacebookService.FacebookProfile facebookProfile= facebook.getSocialProfile(getActivity(), token );
                hideProgressDialog();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindDataToForm(facebookProfile);
                    }
                });

            }
            catch(Exception e)
            {
                showToastOnUIThread(e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFacebookConnect();
                    }
                });

            }
            finally {

                hideProgressDialog();
            }
            return null;
        }
    };

    private void showFacebookConnect() {
        setVisibleChildView(vg_facebookConnect);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK && requestCode == BaseActivity.FACEBOOK_SIGNIN) {

            String access_token = data.getStringExtra("access_token");
            String access_secret = data.getStringExtra("access_secret");

            ((Application)getActivity().getApplication()).putInAppPreference("facebook_access_token", access_token);
            ((Application)getActivity().getApplication()).putInAppPreference("facebook_access_secret", access_secret);

            handleDataNotPresentOnServer();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        //AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void bindDataToForm(FacebookService.FacebookProfile value) {

        setVisibleChildView(vg_facebookForm);
        if(value != null)
        {
            workplace.setText(value.getWorkspace());
            city.setText(value.getCity());
            dob.setText(value.getDob());
            relationshipStatus.setText(value.getRelationshipStatus());
            hometown.setText(value.getHometown());
            connectedAs.setText(value.getConnectedAs());
        }
    }

    @Override
    public FacebookService.FacebookProfile getDataFromForm(FacebookService.FacebookProfile base) {
        if(base == null)
            base = new FacebookService.FacebookProfile();

        base.setHometown(hometown.getText().toString());
        base.setRelationshipStatus(relationshipStatus.getText().toString());
        base.setCity(city.getText().toString());
        base.setConnectedAs(connectedAs.getText().toString());
        base.setWorkspace(workplace.getText().toString());
        base.setDob(dob.getText().toString());

        return base;
    }
}
