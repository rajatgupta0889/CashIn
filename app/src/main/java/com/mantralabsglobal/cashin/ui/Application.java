package com.mantralabsglobal.cashin.ui;

import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mantralabsglobal.cashin.ui.view.MonthIncomeView;
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

    }

    public RestClient getRestClient() {
        return restClient;
    }

}


