package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PerfiosClient;
import com.mantralabsglobal.cashin.service.PerfiosService;
import com.mantralabsglobal.cashin.utils.PerfiosUtils;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PerfiosActivity extends BaseActivity {

    private static final String TAG = PerfiosActivity.class.getSimpleName();
    @InjectView(R.id.webview)
    WebView mWebView;
    PerfiosService perfiosService;
    private String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_signin);
        ButterKnife.inject(this);
        perfiosService = PerfiosClient.getDefault().getPerfoisService();
        transactionId = UUID.randomUUID().toString();
        initWebView();
        //getInstitutions();
        startProcess();
    }

    protected void getInstitutions(){
        PerfiosService.InstitutionsPayload payload = new PerfiosService.InstitutionsPayload();
        payload.setApiVersion(getString(R.string.perfios_api_version));
        payload.setVendorId(getString(R.string.perfios_vendorId));
        payload.setDestination("netbankingFetch");
        try {
            String payloadSignature = PerfiosUtils.getPayloadSignature(payload, PerfiosClient.getDefault().getPrivateKey());
            perfiosService.getInstitutions(PerfiosUtils.serialize(payload), payloadSignature, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i(TAG, s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i(TAG, error.getMessage());
                }
            });
        } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | NoSuchProviderException | NoSuchPaddingException | SignatureException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void startProcess()
    {
        PerfiosService.StartProcessPayload payload = new PerfiosService.StartProcessPayload();
        payload.setApiVersion(getString(R.string.perfios_api_version));
        payload.setVendorId(getString(R.string.perfios_vendorId));
        payload.setTransactionId(transactionId);
        try {
            payload.setEmailId( PerfiosUtils.encrypt(getCashInApplication().getAppUser(), PerfiosClient.getDefault().getPublicKey()));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        payload.setDestination("netbankingFetch");
        payload.setLoanAmount(100000);
        payload.setLoanDuration(24);
        payload.setLoanType("Personal");

        try {
            String payloadSignature = PerfiosUtils.getPayloadSignature(payload, PerfiosClient.getDefault().getPrivateKey());
            //boolean isValid = PerfiosUtils.validateSignature(payloadSignature, PerfiosUtils.serialize(payload), PerfiosClient.getDefault().getPublicKey());
            String postData = "payload=" + PerfiosUtils.serialize(payload) + "&signature=" + payloadSignature;

            mWebView.postUrl(getString(R.string.perfios_url) + "/start", EncodingUtils.getBytes(postData, "UTF-8"));

        } catch (IllegalBlockSizeException | UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            e.printStackTrace();
        }

    }

    protected void initWebView(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.endsWith("report"))
                {
                    Intent intent = new Intent();
                    intent.putExtra("transactionId", transactionId);
                    setResult(RESULT_OK, intent);
                    finish();

                }
                else {
                    view.loadUrl(url);


                }
                return true;
            }
        });
    }

}
