package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 6/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected ProgressDialog progressDialog;

    /* Client used to interact with Google APIs. */
    protected GoogleApiClient mGoogleApiClient;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    protected boolean mShouldResolve = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
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

    protected GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            // onConnected indicates that an account was selected on the device, that the selected
            // account has granted any requested permissions to our app and that we were able to
            // establish a service connection to Google Play services.
            Log.d(TAG, "onConnected:" + bundle);
            mShouldResolve = false;
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            // Show the signed-in UI
            handleGooglePlusConnectionSuccess(email);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // Could not connect to Google Play Services.  The user needs to select an account,
            // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
            // ConnectionResult to see possible error codes.
            Log.d(TAG, "onConnectionFailed:" + connectionResult);

            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(BaseActivity.this, RC_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    // Could not resolve the connection result, show the user an
                    // error dialog.
                    showToastOnUIThread(connectionResult.toString());
                }
            } else {
                // Show the signed-out UI
                //showSignedOutUI();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    public void googlePlusSignIn() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        showProgressDialog(getString(R.string.title_please_wait), getString(R.string.signing_in), true, false);
    }

    protected void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    protected void handleGooglePlusConnectionSuccess(String email)
    {
        //Handle in derived activities
    }

}
