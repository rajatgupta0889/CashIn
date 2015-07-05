package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.FacebookService;
import com.mantralabsglobal.cashin.social.Facebook;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class FacebookFragment extends BaseBindableFragment<FacebookService.FacebookProfile>  {

    private EditText dobEditText;
    private SocialAuthAdapter socialAuthAdapter;

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
    public LoginButton loginButton;

    CallbackManager callbackManager;

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


        List<String> permissions = Arrays.asList("public_profile","user_birthday", "user_hometown", "user_location", "user_relationship_details", "user_work_history");
        loginButton.setReadPermissions(permissions);
        // If using in a fragment
        //loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        showToastOnUIThread(loginResult.getAccessToken().toString());
                        populateFaceBookProfile(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showToastOnUIThread(exception.getMessage());
                    }
                });

        registerChildView(vg_facebookConnect, View.VISIBLE);
        registerChildView(vg_facebookForm, View.GONE);
        reset(false);



    }

    protected void populateFaceBookProfile(AccessToken token)
    {
        Facebook.getFacebookProfile(token, new SocialBase.SocialListener<FacebookService.FacebookProfile>() {
            @Override
            public void onSuccess(FacebookService.FacebookProfile facebookProfile) {
                bindDataToForm(facebookProfile);
            }

            @Override
            public void onFailure(String message) {

            }
        });
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
        Context context = loginButton.getContext();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null)
        {
            populateFaceBookProfile(accessToken);
        }
        else {
            showFacebookConnect();
        }
    }

    private void showFacebookConnect() {
        setVisibleChildView(vg_facebookConnect);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(getActivity());
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
