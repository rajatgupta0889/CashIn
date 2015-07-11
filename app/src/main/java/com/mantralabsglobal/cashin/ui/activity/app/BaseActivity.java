package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;

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

    public static final String APP_PREFERENCE = "APP_PREFERENCE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String EMPTY_STRING = "";
    public static final int GOOGLE_PLUS_LOGIN_REQUEST_CODE = 1000;
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 2000;
    public static final int SELECT_PHOTO_FROM_GALLERY = 3000;
    public static final int SELFIE_CAPTURE = 4000;
    public static final int CROP_SELFIE = 5000;


    protected void putInAppPreference(String key, String value) {
        appPreference.edit().putString(key, value).apply();
    }

    public String getUserName()
    {
        return appPreference.getString(USER_NAME, null);
    }

    public String getUserId()
    {
        return appPreference.getString(USER_ID, null);
    }


    protected void registerAndLogin(final String userName, boolean userExists, final IAuthListener listener) {
        AuthenticationService authService = ((com.mantralabsglobal.cashin.ui.Application) getApplication()).getRestClient().getAuthenticationService();
        AuthenticationService.UserPrincipal up = new AuthenticationService.UserPrincipal();
        up.setEmail(userName);
        up.setPassword("dummy");
        if(userExists) {
            authService.authenticateUser(up, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    putInAppPreference(USER_ID, authenticatedUser.getId());
                    listener.onSuccess();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (RetrofitUtils.isUserNotRegisteredError(error))
                        registerAndLogin(userName, false, listener);
                    else {
                        showToastOnUIThread(error.getMessage());
                        listener.onFailure(error);
                    }
                }
            });
        }
        else
        {
            AuthenticationService.NewUser nu = new AuthenticationService.NewUser(up.getEmail(),up.getPassword());

            authService.registerUser(nu, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    registerAndLogin(userName, true, listener);
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
        appPreference = getSharedPreferences(APP_PREFERENCE, 0);
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

}
