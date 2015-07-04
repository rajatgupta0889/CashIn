package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.util.Arrays;

import butterknife.InjectView;

/**
 * Created by pk on 13/06/2015.
 */
public class FacebookFragment extends BaseFragment  {

    private EditText dobEditText;
    private SocialAuthAdapter socialAuthAdapter;

    @InjectView(R.id.cet_workplace)
    public CustomEditText workplace;

    @InjectView(R.id.cet_city)
    public CustomEditText city;

    @InjectView(R.id.cet_hometown)
    public CustomEditText hometown;

    @InjectView(R.id.cet_relationshipStatus)
    public CustomEditText relationshipStatus;

    @InjectView(R.id.cet_dob)
    public BirthDayView dob;

    @InjectView(R.id.rl_facebook_details)
    public ViewGroup viewGroup_facebookForm;

    @InjectView(R.id.btn_facebook_connect)
    public LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnFacebookConnect = (Button) view.findViewById(R.id.btn_facebook_connect);


        loginButton.setReadPermissions(Arrays.asList("public_profile", "email","user_birthday"));
        // If using in a fragment
        //loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        showToastOnUIThread(loginResult.getAccessToken().toString());
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                if(profile2 != null) {
                                    Log.v("facebook - profile", profile2.getFirstName());
                                    showToastOnUIThread(profile2.getFirstName());
                                    mProfileTracker.stopTracking();
                                }
                            }
                        };
                        mProfileTracker.startTracking();
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

        registerChildView(getCurrentView().findViewById(R.id.ll_facebook_connect), View.VISIBLE);
        registerChildView(viewGroup_facebookForm, View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showFacebookProfileForm(FacebookService.FacebookProfile fbProfile)
    {
        if(fbProfile != null)
        {
            workplace.setText(fbProfile.getWorkspace());
            city.setText(fbProfile.getCity());
            //dob.setText(fbProfile.getDob());
            relationshipStatus.setText(fbProfile.getRelationshipStatus());
            hometown.setText(fbProfile.getHometown());
        }
        setVisibleChildView(viewGroup_facebookForm);
    }

    /*private SocialAuthListener<Profile> profileSocialAuthListener = new SocialAuthListener<Profile>() {
        @Override
        public void onExecute(String s, Profile profile) {
            FacebookService.FacebookProfile facebookProfile = new FacebookService.FacebookProfile();
            facebookProfile.setCity(profile.getLocation());
            facebookProfile.setDob(profile.getDob().toString());
            //facebookProfile.setRelationshipStatus(profile.get);
            showFacebookProfileForm(facebookProfile);
            hideProgressDialog();
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            hideProgressDialog();
            showToastOnUIThread(socialAuthError.getMessage());
        }
    };*/

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(getActivity());
    }
}
