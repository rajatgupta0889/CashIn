package com.mantralabsglobal.cashin.fragment.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 14/06/2015.
 */
public class IncomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.income, container, false);
        return view;
    }
}
