package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/30/2015.
 */
public abstract class BaseBindableFragment<T> extends BaseFragment implements Bindable<T> {

    private static final String TAG = "BaseBindableFragment";
    private boolean isFormValid;
    private Validator validator;
    protected T serverCopy;
    protected boolean isDataPresentOnServer = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        validator = new Validator(this);

        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                if(getFormView()!= null && getFormView().isShown() && getFormView().isEnabled())
                    isFormValid = true;
                else
                    isFormValid = false;
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError ve : errors) {
                    if (ve.getView() instanceof CustomEditText) {
                        ((CustomEditText) ve.getView()).getEditText().setError(ve.getFailedRules().get(0).getMessage(getActivity()));
                    }
                }
                isFormValid = false;
            }
        });

    }

    public boolean isFormValid()
    {
        try {
            validator.validate(false);
        }
        catch(Exception e)
        {
            Log.w(TAG, e.getMessage());
        }
        return isFormValid ;
    }

    @Optional
    @Override
    @OnClick(R.id.btn_save)
    public void save() {
        save(true);
    }

    public void save(boolean force) {
        if(isFormValid())
        {
            if(serverCopy == null)
            {
                showProgressDialog(getString(R.string.waiting_for_server));
                T formData = getDataFromForm(null);
                onCreate(formData, saveCallback);
            }
            else
            {
                T data = cloneThroughJson(serverCopy);
                T updatedData = getDataFromForm(data);
                if(force || isDataChanged(updatedData)) {
                    showProgressDialog(getString(R.string.waiting_for_server));
                    onUpdate(updatedData, saveCallback);
                }
            }

        }
    }



    @SuppressWarnings("unchecked")
    protected T cloneThroughJson(T t) {
        Gson gson = new Gson();
        String json = gson.toJson(t);
        return (T) gson.fromJson(json, t.getClass());
    }

    protected boolean isDataChanged(T updatedData) {
        Gson gson = new Gson();
        return !gson.toJsonTree(serverCopy).equals(gson.toJsonTree(updatedData));
    }

    protected abstract void onUpdate(T updatedData, Callback<T> saveCallback);

    protected abstract void onCreate(T updatedData, Callback<T> saveCallback);

    @Optional
    @OnClick(R.id.btn_reset)
    public void onResetClick()
    {
        reset(true);
    }

    @Override
    public void reset(boolean force)
    {
        if(force || (serverCopy == null && isDataPresentOnServer))
        {
            showProgressDialog(getString(R.string.waiting_for_server));
            loadDataFromServer(dataCallback);
        }
        else
        {
            dataCallback.success(serverCopy,null);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        save(false);
    }


    protected abstract void loadDataFromServer(Callback<T> dataCallback);

    private Callback<T> saveCallback = new Callback<T>() {
        @Override
        public void success(T value, Response response) {
            serverCopy = value;
            if(beforeBindDataToForm(value, response))
                bindDataToForm(value);
            //showToastOnUIThread(getString(R.string.save_sucess));
            hideProgressDialog();
        }

        @Override
        public void failure(RetrofitError error) {
            hideProgressDialog();
            if(getCurrentView() instanceof CoordinatorLayout) {
                Snackbar snackbar = Snackbar
                        .make((CoordinatorLayout) getCurrentView(), "Failed to save data. Error: " + error.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save();
                            }
                        });
                snackbar.show();
            }
            //showToastOnUIThread(error.getMessage());
        }
    };

    protected boolean beforeBindDataToForm(T value, Response response) {
        return true;
    }

    private Callback<T> dataCallback = new Callback<T>() {
        @Override
        public void success(T value, Response response) {
            hideProgressDialog();
            if(response!= null && response.getBody() != null && response.getBody().length()<=2)
                value = null;
            serverCopy = value;
            if(serverCopy != null) {
                bindDataToForm(value);
                isDataPresentOnServer = true;
            }
            else {
                isDataPresentOnServer = false;
                handleDataNotPresentOnServer();
            }
            //showToastOnUIThread(getString(R.string.d));

        }

        @Override
        public void failure(RetrofitError error) {
            hideProgressDialog();
            if(RetrofitUtils.isDataNotOnServerError(error))
            {
                isDataPresentOnServer = false;
                handleDataNotPresentOnServer();
            }
            else {
                if(getCurrentView() instanceof CoordinatorLayout) {
                    Snackbar snackbar = Snackbar
                            .make((CoordinatorLayout) getCurrentView(), "Failed to query server. Error: " + error.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reset(true);
                                }
                            });
                    snackbar.show();
                }
            }
        }
    };

    protected abstract void handleDataNotPresentOnServer();

    protected abstract View getFormView();
}
