package com.mantralabsglobal.cashin.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.views.CustomEditText;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by pk on 14/06/2015.
 */
public class IncomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar cal = Calendar.getInstance();
        String month_1 = getMonthForInt(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        cal.add(Calendar.MONTH,-1);
        String month_2 = getMonthForInt(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        cal.add(Calendar.MONTH,-1);
        String month_3 = getMonthForInt(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        ((CustomEditText) view.findViewById(R.id.cc_month_one)).setLabel(month_1);
        ((CustomEditText) view.findViewById(R.id.cc_month_two)).setLabel(month_2);
        ((CustomEditText) view.findViewById(R.id.cc_month_three)).setLabel(month_3);
    }

    String getMonthForInt(int num, int year) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month + " " + year;
    }
}
