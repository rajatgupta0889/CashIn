package com.mantralabsglobal.cashin.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.views.BirthDayView;
import com.mantralabsglobal.cashin.views.CustomEditText;
import com.mantralabsglobal.cashin.views.SonOfSpinner;

/**
 * Created by pk on 13/06/2015.
 */
public class PANCardFragment extends BaseFragment  {

    private BirthDayView dobEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_pan_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnEdit = (Button) view.findViewById(R.id.enterPANDetailsButton);
        btnEdit.setOnClickListener(listener);

        dobEditText = (BirthDayView)view.findViewById(R.id.cc_dob);

        SonOfSpinner relation = (SonOfSpinner) view.findViewById(R.id.cs_sonOf);

        registerChildView(getCurrentView().findViewById(R.id.enterPANDetailLayout), View.GONE);
        registerChildView(getCurrentView().findViewById(R.id.panCardSnapLayout), View.VISIBLE);
        registerFloatingActionButton((FloatingActionButton)getCurrentView().findViewById(R.id.fab_launchOCR), getCurrentView().findViewById(R.id.enterPANDetailLayout));

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == getCurrentView().findViewById(R.id.enterPANDetailsButton).getId())
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.enterPANDetailLayout));
            }
            else
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.panCardSnapLayout));
            }

        }
    };

}
