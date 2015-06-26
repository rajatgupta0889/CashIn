package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.FacebookService;
import com.mantralabsglobal.cashin.views.BirthDayView;
import com.mantralabsglobal.cashin.views.CustomEditText;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

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

        socialAuthAdapter = new SocialAuthAdapter(dialogListener);

        registerChildView(getCurrentView().findViewById(R.id.ll_facebook_connect), View.VISIBLE);
        registerChildView(viewGroup_facebookForm, View.GONE);
    }

    @OnClick(R.id.btn_facebook_connect)
    public void facebook_connect_click()
    {
        showProgressDialog(getString(R.string.waiting_for_facebook));

        socialAuthAdapter.authorize(getActivity(), SocialAuthAdapter.Provider.FACEBOOK);
    }

    private DialogListener dialogListener = new DialogListener() {
        @Override
        public void onComplete(Bundle bundle) {
            socialAuthAdapter.getUserProfileAsync(FacebookFragment.this.profileSocialAuthListener);
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            hideProgressDialog();
            showToastOnUIThread(socialAuthError.getMessage());
        }

        @Override
        public void onCancel() {
            hideProgressDialog();
        }

        @Override
        public void onBack() {
            hideProgressDialog();
        }
    };

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

    private SocialAuthListener<Profile> profileSocialAuthListener = new SocialAuthListener<Profile>() {
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
    };

}
