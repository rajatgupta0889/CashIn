package com.mantralabsglobal.cashin;

import com.mantralabsglobal.cashin.rest.RestClient;

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

    }

    public RestClient getRestClient() {
        return restClient;
    }

}


