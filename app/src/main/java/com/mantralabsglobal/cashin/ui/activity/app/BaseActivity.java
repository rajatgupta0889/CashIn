package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";


    protected ProgressDialog progressDialog;
    SharedPreferences appPreference = null;

    public static final int GOOGLE_PLUS_LOGIN_REQUEST_CODE = 1000;
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 2000;
    public static final int SELECT_PHOTO_FROM_GALLERY = 3000;
    public static final int SELFIE_CAPTURE = 4000;
    public static final int CROP_SELFIE = 5000;
    public static final int IMAGE_CAPTURE_PAN_CARD = 6000;
    public static final int IMAGE_CROP_PAN_CARD = 6001;
    public static final int IMAGE_CAPTURE_BUSINESS_CARD = 7000;
    public static final int IMAGE_CROP_BUSINESS_CARD = 7001;
    public static final int LINKEDIN_SIGNIN = 8000;
    public static final int FACEBOOK_SIGNIN = 9000;
    protected static final int REQ_SIGN_IN_REQUIRED = 10000;
    public static final int CONTACT_PICKER = 11000;

    protected void putInAppPreference(String key, String value) {
        appPreference.edit().putString(key, value).apply();
    }

    protected Application getCashInApplication()
    {
        return (Application) getApplication();
    }

    public String getUserName()
    {
        return getCashInApplication().getAppUser();
    }

    public String getUserId()
    {
        return getCashInApplication().getAppUserId();
    }


    protected void registerAndLogin(final String userName, final String token,  boolean userExists, final IAuthListener listener) {
        AuthenticationService authService = ((com.mantralabsglobal.cashin.ui.Application) getApplication()).getRestClient().getAuthenticationService();
        AuthenticationService.UserPrincipal up = new AuthenticationService.UserPrincipal();
        up.setEmail(userName);
        up.setToken(token);
        if(userExists) {

            authService.authenticateUser(up, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    getCashInApplication().setAppUserId(authenticatedUser.getId());
                    getCashInApplication().setAppUserName(authenticatedUser.getEmail());
                    getCashInApplication().setGoogleToken(token);
                    listener.onSuccess();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (RetrofitUtils.isUserNotRegisteredError(error))
                        registerAndLogin(userName,token, false, listener);
                    else {
                        showToastOnUIThread(error.getMessage());
                        listener.onFailure(error);
                    }
                }
            });
        }
        else
        {
            AuthenticationService.NewUser nu = new AuthenticationService.NewUser(up.getEmail(),"");

            authService.registerUser(nu, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    registerAndLogin(userName,token, true, listener);
                }

                @Override
                public void failure(RetrofitError error) {
                    showToastOnUIThread(error.getMessage());
                    listener.onFailure(error);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreference = getCashInApplication().getAppPreference();
        progressDialog = new ProgressDialog(this);
    }

    protected void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    protected void hideProgressDialog()
    {
        progressDialog.dismiss();
    }

    protected void showToastOnUIThread(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected interface ServerResponseListener{
        public void onSuccess();
        public void onError(RetrofitError error);
    }

    public interface IAuthListener{
        public void onSuccess();
        public void onFailure(Exception exp);
    }

    protected abstract class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        private String email;
        @Override
        protected String doInBackground(String... params) {

            //String scope = String.format("oauth2:server:client_id:%s:api_scope:%s", activity.getString(R.string.server_client_id), TextUtils.join(" ", Arrays.asList(Scopes.PROFILE, Scopes.PLUS_LOGIN)));
            String scope = "oauth2:" + Scopes.PROFILE + " " + Scopes.PLUS_LOGIN;
            String serverToken = null;

            try {
                email = params[0];
                serverToken = GoogleAuthUtil.getToken(BaseActivity.this.getBaseContext(), params[0], scope);

            } catch (IOException e) {
                Log.e(TAG, "Failed to get token for server", e);
                showToastOnUIThread(e.getMessage());
            } catch (UserRecoverableAuthException e) {
                BaseActivity.this.startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                showToastOnUIThread(e.getMessage());
                Log.e(TAG, "Failed to get token for server", e);
            }
            return serverToken;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            afterTokenRecieved(email, s);
        }

        protected abstract void afterTokenRecieved(String email, String token);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
