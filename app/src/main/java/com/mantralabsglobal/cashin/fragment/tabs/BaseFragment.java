package com.mantralabsglobal.cashin.fragment.tabs;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.fragment.DatepickerDialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pk on 6/20/2015.
 */
public abstract class BaseFragment extends Fragment {

    private ArrayAdapter<CharSequence> relationAdapter;
    private ArrayAdapter<CharSequence> genderAdapter;
    private View currentView;
    private List<View> childViews = new ArrayList<>();
    private Map<View, List<FloatingActionButton>> floatingActionButtonViewMap = new HashMap<>();
    private View visibleChildView;

    private ProgressDialog mProgressDialog;

    protected void showDialog() {
        if (mProgressDialog == null) {
            setProgressDialog();
        }
        mProgressDialog.show();
    }

    protected void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void setProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Thinking...");
        mProgressDialog.setMessage("Doing the action...");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentView = view;
    }

    protected void registerChildView(View view, int visibility)
    {
        childViews.add(view);
        view.setVisibility(visibility);
        if(visibility == View.VISIBLE)
        {
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
        if(!fabList.contains(fab))
        {
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
            if(visibleChildView != null) {
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

    public int computeAge(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar myBirthDate = Calendar.getInstance();
        myBirthDate.clear();
        myBirthDate.set(year, monthOfYear, dayOfMonth);
        Calendar now = Calendar.getInstance();
        Calendar clone = (Calendar) myBirthDate.clone(); // Otherwise changes are been reflected.
        int years = -1;
        while (!clone.after(now)) {
            clone.add(Calendar.YEAR, 1);
            years++;
        }
        return years;
    }

    protected View getCurrentView(){
        return currentView;
    }

    protected ArrayAdapter<CharSequence> getRelationAdapter()
    {
        if(relationAdapter == null)
        {
            relationAdapter = ArrayAdapter.createFromResource(getCurrentView().getContext(), R.array.relationship_array, android.R.layout.simple_spinner_item);
            relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return relationAdapter;
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

    protected void initDateOfBirthEditText(final EditText et_dob, final TextView tv_age)
    {
        et_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
                Date defaultDate = null;
                try {
                    defaultDate = dateFormat.parse(et_dob.getText().toString());
                } catch (ParseException e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        defaultDate = sdf.parse(getString(R.string.default_dateof_birth));

                    } catch (ParseException e1) {

                        e1.printStackTrace();
                    }
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new DatepickerDialogFragment( new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());

                        et_dob.setText(dateFormat.format(cal.getTime()));
                        tv_age.setText(computeAge(year, monthOfYear, dayOfMonth) + " Years");
                    }
                }, defaultDate);
                newFragment.show(ft, "date_dialog");

            }
        });
    }
}
