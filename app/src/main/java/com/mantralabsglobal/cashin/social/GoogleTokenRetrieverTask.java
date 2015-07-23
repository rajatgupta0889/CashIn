package com.mantralabsglobal.cashin.social;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;

import java.io.IOException;

/**
 * Created by hello on 7/23/2015.
 */
public abstract class GoogleTokenRetrieverTask extends AsyncTask<Context, Void, String> {

    private static final String GMAIL_SCOPE = "https://www.googleapis.com/auth/gmail.readonly";

    private static final String TAG = GoogleTokenRetrieverTask.class.getSimpleName();

    public String executeSync(Context context)
    {
        return doInBackground(context);
    }

    @Override
    protected String doInBackground(Context... params) {

        //String scope = String.format("oauth2:server:client_id:%s:api_scope:%s", activity.getString(R.string.server_client_id), TextUtils.join(" ", Arrays.asList(Scopes.PROFILE, Scopes.PLUS_LOGIN)));
        String scope = "oauth2:" + Scopes.PROFILE + " " + Scopes.PLUS_LOGIN + " " + GMAIL_SCOPE;
        String serverToken = null;
        Context context = params[0];
        try {
            serverToken = GoogleAuthUtil.getToken(context, getEmail() , scope);

        } catch (IOException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
        } catch (UserRecoverableAuthException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
            //baseActivity.startActivityForResult(e.getIntent(), BaseActivity.REQ_SIGN_IN_REQUIRED);
        } catch (GoogleAuthException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
//            baseActivity.showToastOnUIThread(e.getMessage());
        }
        return serverToken;
    }

    protected abstract String getEmail();

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        afterTokenRecieved(getEmail(), s);
    }

    public void onException(UserRecoverableAuthException e) {
        Log.e(TAG, "UserRecoverableAuthException", e);
    }

    public void onException(IOException e) {
        Log.e(TAG,"IOException" ,e);
    }

    public void onException(GoogleAuthException e) {
        Log.e(TAG, "GoogleAuthException", e);
    }

    protected abstract void afterTokenRecieved(String email, String token);

}