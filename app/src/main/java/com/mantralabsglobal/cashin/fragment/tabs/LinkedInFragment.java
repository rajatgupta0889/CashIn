package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.views.CustomEditText;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by pk on 13/06/2015.
 */
public class LinkedInFragment extends BaseFragment  {

    @InjectView(R.id.btn_linkedIn_connect)
    Button btn_linkedIn;

    @InjectView(R.id.ll_linkedIn_connect)
    ViewGroup vg_linkedInConnect;

    @InjectView(R.id.rl_linkedin_details)
    ViewGroup vg_linkedInProfile;

    @InjectView(R.id.cs_job_title)
    CustomEditText jobTitle;
    @InjectView(R.id.cs_company)
    CustomEditText company;
    @InjectView(R.id.cs_time_period)
    CustomEditText period;

    SocialAuthAdapter socialAuthAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_linkedin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socialAuthAdapter = new SocialAuthAdapter(new ResponseListener());

        registerChildView(vg_linkedInConnect, View.VISIBLE);
        registerChildView(vg_linkedInProfile, View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @OnClick(R.id.btn_linkedIn_connect)
    public void onConnectClick()
    {
        socialAuthAdapter.authorize(getActivity(), SocialAuthAdapter.Provider.LINKEDIN);
    }

    private final class ResponseListener implements DialogListener
    {
        public void onComplete(Bundle values) {
            socialAuthAdapter.getUserProfileAsync(new ProfileDataListener());

        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.e("LinkedInFragment", socialAuthError.getMessage(), socialAuthError);
        }

        @Override
        public void onCancel() {
            Log.w("LinkedInFragment", "Linkedin connect cancelled");
        }

        @Override
        public void onBack() {
            Log.w("LinkedInFragment", "Linkedin connect cancelled by back");
        }
    }

    // To receive the profile response after authentication
    private final class ProfileDataListener implements SocialAuthListener<Profile> {

        @Override
        public void onExecute(String s, Profile profileMap) {
            Log.d("LinkedInFragment", "Receiving Data");
            Log.d("LinkedInFragment", "Validate ID         = " + profileMap.getValidatedId());
            Log.d("LinkedInFragment", "First Name          = " + profileMap.getFirstName());
            Log.d("LinkedInFragment", "Last Name           = " + profileMap.getLastName());
            Log.d("LinkedInFragment", "Email               = " + profileMap.getEmail());
            Log.d("LinkedInFragment", "Country                  = " + profileMap.getCountry());
            Log.d("LinkedInFragment", "Language                 = " + profileMap.getLanguage());
            Log.d("LinkedInFragment", "Location                 = " + profileMap.getLocation());
            Log.d("LinkedInFragment", "Profile Image URL  = " + profileMap.getProfileImageURL());
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Toast.makeText(getActivity(), "Failed to get profile information", Toast.LENGTH_LONG);
        }
    }


}
