package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EStatementFragment extends BaseFragment
{
    private static final String TAG = EStatementFragment.class.getSimpleName();
    final private List<String> SCOPES = Arrays.asList(new String[]{
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/gmail.readonly"
    });

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_e_statement, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scanGmailForBankStatements();
    }

    protected void scanGmailForBankStatements() {
        String gmailAccount =  ((Application)getActivity().getApplication()).getGmailAccount();
        if(TextUtils.isEmpty(gmailAccount))
        {
            //Get the gmail account from user
            Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
            getActivity().startActivityForResult(googlePicker, BaseActivity.PICK_ACCOUNT_REQUEST);

        }
        else
        {
            requestForGmailToken(gmailAccount);
        }
    }

    protected void requestForGmailToken(final String gmailAccount) {
        new GoogleTokenRetrieverTask() {

            @Override
            protected String getEmail() {
                return gmailAccount;
            }

            protected String getScope()
            {
                return String.format("oauth2:server:client_id:%s:api_scope:%s", getResources().getString(R.string.server_client_id), TextUtils.join(" ", SCOPES));

  //              return super.getScope() + " " + GMAIL_SCOPE;
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
                //TODO: Invoke server API to scan email
                Log.d(TAG, token);
            }
        }.execute(getActivity());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == BaseActivity.PICK_ACCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.d(TAG, "Account Name=" + accountName);
            //Uncomment below line to remember the gmail account
            ((Application)getActivity().getApplication()).setGmailAccount(accountName);
            requestForGmailToken(accountName);
        }
        else if (requestCode == BaseActivity.REQ_SIGN_IN_REQUIRED && resultCode == Activity.RESULT_OK) {
            scanGmailForBankStatements();
        }
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