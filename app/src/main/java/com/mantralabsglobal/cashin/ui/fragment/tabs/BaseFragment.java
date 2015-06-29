package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by pk on 6/20/2015.
 */
public abstract class BaseFragment extends Fragment {

    private ArrayAdapter<CharSequence> genderAdapter;
    private View currentView;
    private List<View> childViews = new ArrayList<>();
    private Map<View, List<FloatingActionButton>> floatingActionButtonViewMap = new HashMap<>();
    private View visibleChildView;
    private boolean isFormValid;

    protected ProgressDialog progressDialog;

    private Validator validator;

    protected void showProgressDialog( String message)
    {
        showProgressDialog(message, true, false);
    }
    protected void showProgressDialog( final String message, final boolean indeterminate, final boolean cancelable)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setTitle(getString(R.string.title_please_wait));
                progressDialog.setMessage(message);
                progressDialog.setIndeterminate(indeterminate);
                progressDialog.setCancelable(cancelable);
                progressDialog.show();
            }
        });

    }

    protected void showToastOnUIThread(final String message)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void hideProgressDialog()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        currentView = view;
        progressDialog = new ProgressDialog(getActivity());
        validator = new Validator(this);

        if(this instanceof Bindable<?>)
        {
            validator.setValidationListener(new Validator.ValidationListener() {
                @Override
                public void onValidationSucceeded() {
                    isFormValid=true;
                }

                @Override
                public void onValidationFailed(List<ValidationError> errors) {
                    for (ValidationError ve : errors) {
                        if (ve.getView() instanceof CustomEditText) {
                            ((CustomEditText) ve.getView()).getEditText().setError(ve.getFailedRules().get(0).getMessage(getActivity()));
                        }
                    }
                    isFormValid=false;
                }
            });
        }
        else {
            initValidator(validator);
        }
    }

    protected void initValidator(Validator validator)
    {
        //To Be implemented by sub classes
    }


    protected void registerChildView(View view, int visibility)
    {
        childViews.add(view);
        view.setVisibility(visibility);
        if(visibility == View.VISIBLE) {
            setVisibleChildView(view);
        }
    }

    protected void registerFloatingActionButton(FloatingActionButton fab, View childView)
    {
        List<FloatingActionButton> fabList = floatingActionButtonViewMap.get(childView);
        if(fabList == null)
        {
            fabList = new ArrayList<>();
            floatingActionButtonViewMap.put(childView,fabList);
        }
        if(!fabList.contains(fab)) {
            fabList.add(fab);
            fab.setVisibility(childView.getVisibility());
        }
    }

    protected void setVisibleChildView(View view)
    {
        if(childViews.contains(view))
        {
            view.setVisibility(View.VISIBLE);
            List<FloatingActionButton> fabList = floatingActionButtonViewMap.get(view);
            if(fabList != null)
            {
                for(FloatingActionButton fab : fabList)
                {
                    fab.setVisibility(View.VISIBLE);
                }
            }
            if(visibleChildView != null && visibleChildView != view) {
                visibleChildView.setVisibility(View.GONE);
                fabList = floatingActionButtonViewMap.get(visibleChildView);
                if(fabList != null)
                {
                    for(FloatingActionButton fab : fabList)
                    {
                        fab.setVisibility(View.GONE);
                    }
                }
            }
            visibleChildView = view;
        }
    }

    protected View getCurrentView(){
        return currentView;
    }


    protected ArrayAdapter<CharSequence> getGenderAdapter()
    {
        if(genderAdapter == null)
        {
            genderAdapter = ArrayAdapter.createFromResource(getCurrentView().getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return genderAdapter;
    }

    public Validator getValidator() {
        return validator;
    }


    public boolean canSave()
    {
        getValidator().validate(false);
        if(isFormValid)
        {
              return true;
        }
        return false;
    }


}
