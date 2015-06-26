package com.mantralabsglobal.cashin.rest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AuthenticationService;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by pk on 6/26/2015.
 */
public class RestClient  {
    private static final String BASE_URL = "your base url";
    private AuthenticationService authenticationService;

    public RestClient(Context context)
    {
        Gson gson = new GsonBuilder()
                .setDateFormat(context.getString(R.string.server_date_time_format))
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(context.getString(R.string.server_url))
                .setConverter(new GsonConverter(gson))
                .build();

        authenticationService = restAdapter.create(AuthenticationService.class);
    }

    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }
}
