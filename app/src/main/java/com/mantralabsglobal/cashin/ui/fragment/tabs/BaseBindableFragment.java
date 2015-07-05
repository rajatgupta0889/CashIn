package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

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
                isFormValid = true;
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
        validator.validate(false);
        if(isFormValid)
        {
            return true;
        }
        return false;
    }

    @Optional
    @Override
    @OnClick(R.id.btn_save)
    public void save() {
        if(isFormValid())
        {
            showProgressDialog(getString(R.string.waiting_for_server));
            if(serverCopy == null)
            {
                T formData = getDataFromForm(null);
                onCreate(formData, saveCallback);
            }
            else
            {
                T updatedData = getDataFromForm(serverCopy);
                onUpdate(updatedData, saveCallback);
            }

        }
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

    protected abstract void loadDataFromServer(Callback<T> dataCallback);

    private Callback<T> saveCallback = new Callback<T>() {
        @Override
        public void success(T value, Response response) {
            serverCopy = value;
            bindDataToForm(value);
            showToastOnUIThread(getString(R.string.save_sucess));
            hideProgressDialog();
        }

        @Override
        public void failure(RetrofitError error) {
            hideProgressDialog();
            Snackbar snackbar = Snackbar
                    .make((CoordinatorLayout)getCurrentView(), "Failed to save data. Error: " + error.getMessage() , Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save();
                        }
                    });
            snackbar.show();
            //showToastOnUIThread(error.getMessage());
        }
    };

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
            if(RetrofitUtils.isDataNotOnServer(error))
            {
                isDataPresentOnServer = false;
                handleDataNotPresentOnServer();
            }
            else {
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
    };

    protected abstract void handleDataNotPresentOnServer();
}
