package com.mantralabsglobal.cashin.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.scribe.oauth.OAuthService;

/**
 * Created by pk on 7/4/2015.
 */
public class GooglePlus{

    private static final String TAG = "GooglePlus";
    /* Client used to interact with Google APIs. */
    protected GoogleApiClient mGoogleApiClient;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    protected boolean mShouldResolve = false;

    public void authenticate(Activity activity, final SocialBase.SocialListener<String> listener)
    {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new AuthenticateCallback() {
                    @Override
                    public void onSuccess(String email) {
                        listener.onSuccess(email);
                    }
                })
                .addOnConnectionFailedListener(new ConnectionFailedListener(activity) {
                    @Override
                    public void onFailure(String reason) {
                        listener.onFailure(reason);
                    }
                })
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        mShouldResolve = true;
        mGoogleApiClient.connect();

    }

    protected abstract class AuthenticateCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle bundle) {
            // onConnected indicates that an account was selected on the device, that the selected
            // account has granted any requested permissions to our app and that we were able to
            // establish a service connection to Google Play services.
            Log.d(TAG, "onConnected:" + bundle);
            mShouldResolve = false;
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            onSuccess(email);
        }

        public abstract void onSuccess(String email);

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    protected abstract class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        Activity activity;

        public ConnectionFailedListener(Activity activity)
        {
            this.activity = activity;
        }
        public abstract void onFailure(String reason);
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // Could not connect to Google Play Services.  The user needs to select an account,
            // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
            // ConnectionResult to see possible error codes.
            Log.d(TAG, "onConnectionFailed:" + connectionResult);

            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    onFailure(connectionResult.toString());
                }
            } else {
                onFailure("Signin cancelled");
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

}
