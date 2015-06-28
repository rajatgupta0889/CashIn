package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 13/06/2015.
 */
public class BusinessCardFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_business_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button btnEdit = (Button) view.findViewById(R.id.enterWorkDetailsButton);
        btnEdit.setOnClickListener(listener);

        registerChildView(getCurrentView().findViewById(R.id.ll_business_card_snap), View.VISIBLE);
        registerChildView(getCurrentView().findViewById(R.id.ll_business_card_detail), View.GONE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == getCurrentView().findViewById(R.id.enterWorkDetailsButton).getId())
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.ll_business_card_detail));
            }
            else
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.ll_business_card_snap));
            }

        }
    };

}
