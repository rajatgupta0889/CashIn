package com.mantralabsglobal.cashin.ui.activity.app;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PerfiosClient;
import com.mantralabsglobal.cashin.service.PerfiosService;
import com.mantralabsglobal.cashin.utils.PerfiosUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        startProcess();
    }

    protected void startProcess()
    {
        PerfiosService.StartProcessPayload payload = new PerfiosService.StartProcessPayload();
        payload.setApiVersion(getString(R.string.perfios_api_version));
        payload.setVendorId(getString(R.string.perfios_vendorId));
        payload.setTransactionId(transactionId);
        try {
            payload.setEmailId( PerfiosUtils.base16(PerfiosUtils.encrypt(getCashInApplication().getAppUser().getBytes(), PerfiosClient.getDefault().getPublicKey())));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        payload.setDestination("statement");
        payload.setLoanAmount(100000);
        payload.setLoanDuration(24);
        payload.setLoanType("Personal");

        try {
            perfiosService.startProcess(PerfiosUtils.serialize(payload), PerfiosUtils.getPayloadSignature(payload, PerfiosClient.getDefault().getPrivateKey()), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i(TAG,s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i(TAG,error.getMessage());
                }
            });
        } catch (IllegalBlockSizeException | UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    protected void initWebView(){
        mWebView.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

}
