package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
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
    @Override
    @OnClick(R.id.btn_reset)
    public void reset()
    {
        showProgressDialog(getString(R.string.waiting_for_server));
        loadDataFromServer(dataCallback);
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
            showToastOnUIThread(error.getMessage());
        }
    };

    private Callback<T> dataCallback = new Callback<T>() {
        @Override
        public void success(T value, Response response) {
            hideProgressDialog();
            serverCopy = value;
            if(serverCopy != null)
                bindDataToForm(value);
            else
                handleDataNotPresentOnServer();
            //showToastOnUIThread(getString(R.string.d));

        }

        @Override
        public void failure(RetrofitError error) {
            hideProgressDialog();
            showToastOnUIThread(error.getMessage());
        }
    };

    protected abstract void handleDataNotPresentOnServer();
}