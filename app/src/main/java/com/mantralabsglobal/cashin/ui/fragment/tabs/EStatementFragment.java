package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.EStatementService;
import com.mantralabsglobal.cashin.service.NetBankingService;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EStatementFragment extends BaseBindableFragment<EStatementService.EStatement>
{
    private static final String TAG = EStatementFragment.class.getSimpleName();
    private EStatementService eStatementService;

   /* @InjectView(R.id.btn_scan_gmail)
    BootstrapButton btnScanGmail;*/

    @InjectView(R.id.e_statement_text)
    TextView eStatementText;

    final private List<String> SCOPES = Arrays.asList(new String[]{
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/gmail.readonly"
    });


    @InjectView(R.id.e_statement_view)
    ViewGroup eStatementView;

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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        eStatementService = ((Application)getActivity().getApplication()).getRestClient().geteStatementService();
        /*scanGmailForBankStatements();*/
        reset(false);
    }

    @Override
    public EStatementService.EStatement getDataFromForm(EStatementService.EStatement base) {

        return base;
    }

    @Override
    public void bindDataToForm(final EStatementService.EStatement value) {
        if (value != null) {
            if(value.getStatus() == 1)
                eStatementText.setText("Information already retrieved");
                /*Toast.makeText(getActivity(), "Information already retrieved", Toast.LENGTH_SHORT).show();*/
            else if(value.getStatus() == 0){
                scanGmailForBankStatements();
            }
        }
        else {
            save();
        }
    }

    @Override
    protected void onUpdate(EStatementService.EStatement updatedData, Callback<EStatementService.EStatement> saveCallback) {
        eStatementService.createEStatement(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(EStatementService.EStatement updatedData, Callback<EStatementService.EStatement> saveCallback) {
        eStatementService.createEStatement(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<EStatementService.EStatement> dataCallback) {
        eStatementService.getEStatement(dataCallback);
    }


    @Override
    protected View getFormView() {
        return eStatementView;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }


   /* @OnClick(R.id.btn_scan_gmail)
    public void onScanGmailClick()
    {
        getCashInApplication().setGmailAccount(null);
        scanGmailForBankStatements();
    }*/

    protected void scanGmailForBankStatements() {
        String gmailAccount =  ((Application)getActivity().getApplication()).getGmailAccount();
        if(TextUtils.isEmpty(gmailAccount))
        {
            //Get the gmail account from user
            Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
            getActivity().startActivityForResult(googlePicker, BaseActivity.PICK_ACCOUNT_REQUEST);

        }
       /* else
        {
            btnScanGmail.setText(R.string.change_gmail_account);
            requestForGmailToken(gmailAccount);
        }*/
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
                getActivity().startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
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
                AuthenticationService.UserGoogleAuthCode authCode = new AuthenticationService.UserGoogleAuthCode();
                authCode.setAuthCode(token);
                authCode.setEmail(email);
                getCashInApplication().getRestClient().getAuthenticationService().sendGoogleAuthCode(authCode, new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        Log.i(TAG, "token posted to server");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showToastOnUIThread(error.getMessage());
                    }
                });
                Log.d(TAG, token);
            }
        }.execute(getActivity());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        Log.d(TAG, "On activity result" + requestCode);
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