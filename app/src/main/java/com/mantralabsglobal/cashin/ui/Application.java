package com.mantralabsglobal.cashin.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mantralabsglobal.cashin.ui.view.MonthIncomeView;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/21/2015.
 */
public class Application extends MultiDexApplication{

    private static final String APP_ID = "1442120239426705";
    private static final String APP_NAMESPACE = "pk_cashin_test";
    public static final String APP_PREFERENCE = "APP_PREFERENCE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String EMPTY_STRING = "";

    private RestClient restClient;
    private SharedPreferences appPreference = null;

   /* static{ System.loadLibrary("opencv_java3");}*/

    @Override
    public void onCreate() {
        super.onCreate();
       // SharedObjects.context = this;

        appPreference = getSharedPreferences(APP_PREFERENCE, 0);

        restClient = new RestClient(this);

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

    public String getAppUser()
    {
        return appPreference.getString(USER_NAME, EMPTY_STRING);
    }

    private Interceptor authInterceptor = new Interceptor() {
        @Override
        public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            // try the request
            com.squareup.okhttp.Response response = chain.proceed(request);
            if(response != null && !response.isSuccessful() && response.code() >= 400 && response.code()<=403)
            {
                String json = new String(response.body().bytes());
                Gson gson = new Gson();
                RetrofitUtils.ErrorMessage errorMessage = gson.fromJson(json, RetrofitUtils.ErrorMessage.class);
                if(errorMessage != null && "user is not logged in".equals(errorMessage.getMessage()))
                {
                    AuthenticationService.UserPrincipal up = new AuthenticationService.UserPrincipal();
                    up.setEmail(getAppUser());
                    up.setPassword("dummy");
                    AuthenticationService.AuthenticatedUser au = getRestClient().getAuthenticationService().authenticateUserSync(up);
                }
                Request newRequest = chain.request();
                response = chain.proceed(newRequest);
            }
            return response;
        }
    };

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

    public SharedPreferences getAppPreference() {
        return appPreference;
    }

    public Interceptor getAuthInterceptor() {
        return authInterceptor;
    }

    public String getAppUserId() {
        return appPreference.getString(USER_ID, EMPTY_STRING);
    }

    public void setAppUserId(String userId) {
        appPreference.edit().putString(USER_ID, userId).apply();
    }

    public void putInAppPreference(String key, int value) {
        appPreference.edit().putInt(key, value).apply();
    }

    public void setAppUserName(String appUserName) {
        appPreference.edit().putString(USER_NAME, appUserName ).apply();
    }
}


