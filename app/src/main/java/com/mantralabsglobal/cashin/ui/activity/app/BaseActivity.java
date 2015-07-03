package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mantralabsglobal.cashin.social.GooglePlus;

import retrofit.RetrofitError;

/**
 * Created by pk on 6/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected ProgressDialog progressDialog;
    GooglePlus googlePlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        googlePlus = new GooglePlus();
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googlePlus.onActivityResult(requestCode, resultCode, data);
    }

    protected interface ServerResponseListener{
        public void onSuccess();
        public void onError(RetrofitError error);
    }

}
