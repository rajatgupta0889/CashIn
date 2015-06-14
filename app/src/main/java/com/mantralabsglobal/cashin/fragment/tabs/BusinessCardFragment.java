package com.mantralabsglobal.cashin.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 13/06/2015.
 */
public class BusinessCardFragment extends Fragment {

    ViewFlipper viewFlipper;
    View currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_business_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewFlipper = (ViewFlipper) view.findViewById(R.id.business_card_viewflipper);
        currentView = view;
        Button btnEdit = (Button) view.findViewById(R.id.enterWorkDetailsButton);
        btnEdit.setOnClickListener(listener);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == currentView.findViewById(R.id.enterWorkDetailsButton).getId())
            {
                child = currentView.findViewById(R.id.enterWorkDetailLayout);
            }
            else
            {
                child = currentView.findViewById(R.id.businessCardSnapLayout);
            }
            if(child != null)
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
        }
    };

}
