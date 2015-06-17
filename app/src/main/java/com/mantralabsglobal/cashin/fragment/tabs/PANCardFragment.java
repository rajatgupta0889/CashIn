package com.mantralabsglobal.cashin.fragment.tabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.fragment.DatepickerDialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by pk on 13/06/2015.
 */
public class PANCardFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    View currentView;
    private EditText dobEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_pan_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        currentView = view;
        Button btnEdit = (Button) view.findViewById(R.id.enterPANDetailsButton);
        btnEdit.setOnClickListener(listener);

        dobEditText = (EditText)view.findViewById(R.id.et_dob);

        dobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
                Date defaultDate = null;
                try {
                    defaultDate = dateFormat.parse(dobEditText.getText().toString());
                } catch (ParseException e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        defaultDate = sdf.parse(getString(R.string.default_dateof_birth));

                    } catch (ParseException e1) {

                        e1.printStackTrace();
                    }
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new DatepickerDialogFragment(PANCardFragment.this, defaultDate);
                newFragment.show(ft, "date_dialog");
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spin_relationship);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.relationship_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        currentView.findViewById(R.id.enterPANDetailLayout).setVisibility(View.GONE);
        currentView.findViewById(R.id.panCardSnapLayout).setVisibility(View.VISIBLE);

    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());

        dobEditText.setText(dateFormat.format(cal.getTime()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            currentView.findViewById(R.id.panCardSnapLayout).setVisibility(View.GONE);
            currentView.findViewById(R.id.enterPANDetailLayout).setVisibility(View.GONE);
            if(v.getId() == currentView.findViewById(R.id.enterPANDetailsButton).getId())
            {
                currentView.findViewById(R.id.enterPANDetailLayout).setVisibility(View.VISIBLE);
            }
            else
            {
                currentView.findViewById(R.id.panCardSnapLayout).setVisibility(View.VISIBLE);
            }
            // if(child != null)
            //      viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
        }
    };

}
