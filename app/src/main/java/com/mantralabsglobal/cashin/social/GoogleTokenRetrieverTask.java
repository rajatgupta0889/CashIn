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

    protected static final String GMAIL_SCOPE = "https://www.googleapis.com/auth/gmail.readonly";

    private static final String TAG = GoogleTokenRetrieverTask.class.getSimpleName();

    public String executeSync(Context context)
    {
        return doInBackground(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        String scope = getScope();

        Context context = params[0];
        try {
            return GoogleAuthUtil.getToken(context, getEmail() , scope);

        } catch (IOException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
        } catch (UserRecoverableAuthException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
        } catch (GoogleAuthException e) {
            Log.e(TAG, "Failed to get token for server", e);
            onException(e);
        }
        return null;
    }

    protected String getScope()
    {
        return "oauth2:" + Scopes.PROFILE + " " + Scopes.PLUS_LOGIN;
    }

    protected abstract String getEmail();

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s != null)
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