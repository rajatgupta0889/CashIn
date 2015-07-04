package com.mantralabsglobal.cashin.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mantralabsglobal.cashin.ui.view.MonthIncomeView;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pk on 6/21/2015.
 */
public class Application extends android.app.Application{

    private static final String APP_ID = "1442120239426705";
    private static final String APP_NAMESPACE = "pk_cashin_test";
    private RestClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();
       // SharedObjects.context = this;

        restClient = new RestClient(getBaseContext());

        Validator.registerAdapter(CustomEditText.class, new ViewDataAdapter<CustomEditText, String>() {
            @Override
            public String getData(CustomEditText view) throws ConversionException {
                return view.getEditText().getText().toString();
            }
        });

        Validator.registerAdapter(CustomSpinner.class, new ViewDataAdapter<CustomSpinner, Integer>() {
            @Override
            public Integer getData(CustomSpinner view) throws ConversionException {
                return view.getSpinner().getSelectedItemPosition();
            }
        });

        Validator.registerAdapter(BirthDayView.class, new ViewDataAdapter<BirthDayView, String>() {
            @Override
            public String getData(BirthDayView view) throws ConversionException {
                return view.getEditText().getText().toString();
            }
        });

        Validator.registerAdapter(MonthIncomeView.class, new ViewDataAdapter<MonthIncomeView, String>() {
            @Override
            public String getData(MonthIncomeView view) throws ConversionException {
                return String.valueOf(view.getAmount().toString());
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());

        getKeyHash();

    }

    private void getKeyHash() {
        try {
            PackageInfo info =     getPackageManager().getPackageInfo("com.mantralabsglobal.cashin",     PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public RestClient getRestClient() {
        return restClient;
    }

}


