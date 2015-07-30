package com.mantralabsglobal.cashin.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.social.GoogleTokenRetrieverTask;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pk on 6/21/2015.
 */
public class Application extends MultiDexApplication{

     public static final String APP_PREFERENCE = "APP_PREFERENCE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String GOOGLE_TOKEN = "GOOGLE_TOKEN";
    public static final String EMPTY_STRING = "";
    private static final String TAG = Application.class.getSimpleName();
    private static final String GMAIL_ACCOUNT = "GMAIL_ACCOUNT";

    private RestClient restClient;
    private SharedPreferences appPreference = null;

   /* static{ System.loadLibrary("opencv_java3");}*/

    @Override
    public void onCreate() {
        super.onCreate();
       // SharedObjects.context = this;

        appPreference = getSharedPreferences(APP_PREFERENCE, 0);

        restClient = new RestClient(this);

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
                    up.setToken(getGoogleTokenSync(getBaseContext(), getAppUser()));
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

    public void putInAppPreference(String key, String value) {
        appPreference.edit().putString(key, value).apply();
    }

    public void setAppUserName(String appUserName) {
        Log.i(TAG, "App User Name " + appUserName);
        appPreference.edit().putString(USER_NAME, appUserName ).apply();
    }

    public String getGoogleTokenSync(Context context, final String email)
    {
        GoogleTokenRetrieverTask task = new GoogleTokenRetrieverTask() {
            @Override
            protected String getEmail() {
                return email;
            }
            @Override
            protected void afterTokenRecieved(String email, String token) {
                //Ignore
            }
        };
        return task.executeSync(context);
    }

    public void setGmailAccount(String gmailAccount) {
        Log.i(TAG, "Gmail Account" + gmailAccount);
        appPreference.edit().putString(GMAIL_ACCOUNT, gmailAccount).apply();
    }

    public String getGmailAccount() {
        return appPreference.getString(GMAIL_ACCOUNT, EMPTY_STRING);
    }
}


