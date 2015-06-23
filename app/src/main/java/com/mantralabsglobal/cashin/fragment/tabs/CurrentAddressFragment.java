package com.mantralabsglobal.cashin.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.views.CustomSpinner;

/**
 * Created by pk on 13/06/2015.
 */
public class CurrentAddressFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
        Button btnEdit = (Button) view.findViewById(R.id.editCurrentAddressButton);
        btnEdit.setOnClickListener(listener);

        FloatingActionButton btnGps = (FloatingActionButton) view.findViewById(R.id.gpsLocationFormButton);
        btnGps.setOnClickListener(listener);

        CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_city);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.city_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner = (CustomSpinner) view.findViewById(R.id.cs_state);
        adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner = (CustomSpinner) view.findViewById(R.id.cs_owned_by);
        adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.own_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        registerChildView(view.findViewById(R.id.getLocationFromFormLayout), View.GONE);
        registerChildView(view.findViewById(R.id.getLocationFromGPSLayout), View.VISIBLE);

        registerFloatingActionButton(btnGps, view.findViewById(R.id.getLocationFromFormLayout));

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == getCurrentView().findViewById(R.id.editCurrentAddressButton).getId())
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.getLocationFromFormLayout));
            }
            else
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.getLocationFromGPSLayout));
            }
        }
    };

}
