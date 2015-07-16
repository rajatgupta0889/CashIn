package com.mantralabsglobal.cashin.ui.activity.social;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.LinkedIn;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by pk on 7/16/2015.
 */
public class LinkedinActivity extends BaseActivity {

    final static String APIKEY = "754dods2c70xsz";
    final static String APISECRET = "EfnEG97mqeDpXxCW";
    final static String CALLBACK = "oauth://linkedin";
    private static final String TAG ="LinkedinActivity" ;
    OAuthService mService;
    @InjectView(R.id.linkedin_webview)
     WebView mWebView;
    private Token mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_signin);
        ButterKnife.inject(this);
        mService = LinkedIn.getService(this, CALLBACK);
        new AsyncLinkedInProfileTask().execute();
    }

    private class AsyncLinkedInProfileTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            // Temporary URL
            String authURL = "http://api.linkedin.com/";

            try {
                mRequestToken = mService.getRequestToken();
                authURL = mService.getAuthorizationUrl(mRequestToken);
            }
            catch ( OAuthException e ) {
                e.printStackTrace();
                return null;
            }

            return authURL;
        }

        @Override
        protected void onPostExecute(String authURL) {
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    super.shouldOverrideUrlLoading(view, url);

                    if (url.startsWith("oauth")) {
                        mWebView.setVisibility(WebView.GONE);

                        final String url1 = url;
                        Thread t1 = new Thread() {
                            public void run() {
                                Uri uri = Uri.parse(url1);

                                String verifier = uri.getQueryParameter("oauth_verifier");
                                Verifier v = new Verifier(verifier);
                                Token accessToken = mService.getAccessToken(mRequestToken, v);
                                Intent intent = new Intent();
                                intent.putExtra("linkedin_access_token", accessToken.getToken());
                                intent.putExtra("linkedin_access_secret", accessToken.getSecret());
                                setResult(RESULT_OK, intent);
                                
                                finish();
                            }
                        };
                        t1.start();
                    }

                    return false;
                }
            });

            mWebView.loadUrl(authURL);
        }
    };
}
