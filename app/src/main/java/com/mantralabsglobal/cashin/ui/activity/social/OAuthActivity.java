package com.mantralabsglobal.cashin.ui.activity.social;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.social.LinkedIn;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.social.SocialFactory;
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
public class OAuthActivity extends BaseActivity {

    final static String CALLBACK_FAILURE = "http://localhost:9000?oauth_problem";
    private static final String TAG ="OAuthActivity" ;
    OAuthService mService;
    @InjectView(R.id.webview)
     WebView mWebView;
    private Token mRequestToken;
    SocialBase<?> socialBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_signin);
        ButterKnife.inject(this);
        String socialName = getIntent().getStringExtra("SOCIAL_NAME");
        socialBase = SocialFactory.getSocialHelper(socialName);
        mService = socialBase.getOAuthService(this);
        new AsyncLinkedInProfileTask().execute();
    }

    private class AsyncLinkedInProfileTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            // Temporary URL
            String authURL = "http://api.linkedin.com/";

            try {
                mRequestToken = socialBase.getRequestToken(mService);
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

                    if (url.startsWith(socialBase.getCallBackUrl())) {
                        mWebView.setVisibility(WebView.GONE);

                        final String url1 = url;
                        Thread t1 = new Thread() {
                            public void run() {
                                //Uri uri = Uri.parse(url1);

                                String verifier = socialBase.getVerifierCode(url1);
                                Verifier v = new Verifier(verifier);
                                Token accessToken = mService.getAccessToken(mRequestToken, v);
                                Intent intent = new Intent();
                                intent.putExtra("access_token", accessToken.getToken());
                                intent.putExtra("access_secret", accessToken.getSecret());
                                setResult(RESULT_OK, intent);

                                finish();
                            }
                        };
                        t1.start();
                    }
                    else if(url.contains("oauth_problem")) {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }

                    return false;
                }
            });

            mWebView.loadUrl(authURL);
        }
    };
}
