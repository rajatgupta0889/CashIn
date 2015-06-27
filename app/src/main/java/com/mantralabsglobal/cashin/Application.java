package com.mantralabsglobal.cashin;

import com.mantralabsglobal.cashin.rest.RestClient;
import com.mantralabsglobal.cashin.views.CustomEditText;
import com.mantralabsglobal.cashin.views.CustomSpinner;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

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


    }

    public RestClient getRestClient() {
        return restClient;
    }

}


