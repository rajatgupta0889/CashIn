package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 13/06/2015.
 */
public class LinkedInFragment extends BaseFragment  {

    private EditText dobEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_linkedin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerChildView(getCurrentView().findViewById(R.id.ll_linkedIn_connect), View.VISIBLE);
        registerChildView(getCurrentView().findViewById(R.id.rl_linkedin_details), View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
