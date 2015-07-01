package com.mantralabsglobal.cashin.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AddressService;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by pk on 6/26/2015.
 */
public class RestClient  {
    private static final String BASE_URL = "your base url";
    private AuthenticationService authenticationService;
    private LinkedInService linkedInService;
    private AddressService addressService;
    private AadharService aadharService;
    private PrimaryBankService primaryBankService;

    public RestClient(Context context)
    {
        Gson gson = new GsonBuilder()
                .setDateFormat(context.getString(R.string.server_date_time_format))
                .create();

        OkHttpClient client = new OkHttpClient(); //create OKHTTPClient

        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager); //finally set the cookie handler on client

        OkClient serviceClient = new OkClient(client);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(context.getString(R.string.server_url))
                .setConverter(new GsonConverter(gson))
                .setClient(serviceClient)
                .build();

        authenticationService = restAdapter.create(AuthenticationService.class);
        linkedInService = restAdapter.create(LinkedInService.class);
        addressService = restAdapter.create(AddressService.class);
        aadharService = restAdapter.create(AadharService.class);
        primaryBankService = restAdapter.create(PrimaryBankService.class);
    }

    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    public LinkedInService getLinkedInService() {
        return linkedInService;
    }

    public AddressService getAddressService() {
        return addressService;
    }

    public AadharService getAadharService() {
        return aadharService;
    }

    public  PrimaryBankService getPrimaryBankService()
    {
        return primaryBankService;
    }
}
