package com.mantralabsglobal.cashin.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pk on 17/06/2015.
 */
public class DatepickerDialogFragment extends DialogFragment {

    private  Date defaultDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public DatepickerDialogFragment() {
        // nothing to see here, move along
    }

    @SuppressLint("ValidFragment")
    public DatepickerDialogFragment(DatePickerDialog.OnDateSetListener callback, Date defaultDate) {
        mDateSetListener = (DatePickerDialog.OnDateSetListener) callback;
        this.defaultDate=defaultDate;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(defaultDate);
        return new DatePickerDialog(getActivity(),
                mDateSetListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

}
