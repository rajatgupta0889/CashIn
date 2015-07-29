package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.GooglePlus;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.GetStartedActivity;
import com.mantralabsglobal.cashin.ui.activity.app.IntroSliderActivity;

import java.io.IOException;

public class EStatementFragment extends Fragment
{
    BaseActivity activity;
    GooglePlus googlePlus;
    private String eMail;
    private boolean isExecutedOnce= false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        googlePlus = new GooglePlus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_e_statement, container, false);
        activity = (BaseActivity)getActivity();
        googleAuthentication();
        return view;
    }

   private void  googleAuthentication(){
        if(!isExecutedOnce) {
            activity.showProgressDialog(getString(R.string.title_please_wait), getString(R.string.signing_in), true, false);

            googlePlus.authenticate(getActivity(), true, new SocialBase.SocialListener<String>() {
                @Override
                public void onSuccess(final String email) {
                    eMail = email;
                    isExecutedOnce = true;
                    tokenTask.appendGmailScope(true);
                    tokenTask.execute(getActivity());
                    activity.showToastOnUIThread("Now you have access rights for gmail!");
                }

                @Override
                public void onFailure(String message) {
                    activity.hideProgressDialog();
                    //   Snackbar.make(viewPager, message, Snackbar.LENGTH_LONG).show();
                    startActivityForResult(getActivity().getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
                }
            });
        }
        else {
            activity.showToastOnUIThread("Already Signed In");

        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private GoogleTokenRetrieverTask tokenTask = new GoogleTokenRetrieverTask() {

        @Override
        protected String getEmail() {
            return eMail;
        }

        @Override
        public void onException(UserRecoverableAuthException e) {
            startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
        }

        @Override
        public void onException(IOException e) {
            super.onException(e);
            activity.showToastOnUIThread(e.getMessage());
        }

        @Override
        public void onException(GoogleAuthException e) {
            super.onException(e);
            activity.showToastOnUIThread(e.getMessage());
        }

        @Override
        protected void afterTokenRecieved(String email, String token) {
            activity.registerAndLogin(email, token, true, new BaseActivity.IAuthListener() {
                @Override
                public void onSuccess() {
                    activity.hideProgressDialog();
                }

                @Override
                public void onFailure(Exception exp) {
                    activity.hideProgressDialog();
                    googleAuthentication();
                }
            });
        }
    };

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}