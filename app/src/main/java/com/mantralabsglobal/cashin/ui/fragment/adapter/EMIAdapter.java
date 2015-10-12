package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.EMIService;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.EMIFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.HistoryFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.IncomeFragment;

/**
 * Created by pk on 13/06/2015.
 */
public class EMIAdapter extends ArrayAdapter<EMIService.EMI> {

    Context context;
    int layoutResourceId;
    EMIService.EMI data[] = null;

    public EMIAdapter(Context context, int layoutResourceId, EMIService.EMI[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EMIHolder holder = null;

        if(row == null)
        {

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EMIHolder();
            holder.emiAmount = (EditText)row.findViewById(R.id.emi_amount_text);
            holder.emiDescription = (EditText)row.findViewById(R.id.emi_description_text);

            row.setTag(holder);
        }
        else
        {
            holder = (EMIHolder)row.getTag();
        }

        EMIService.EMI emi = data[position];
        holder.emiAmount.setText(emi.getEmiAmount());
        holder.emiDescription.setText(emi.getEMIDescription());

        return row;
    }

    static class EMIHolder
    {
        EditText emiAmount;
        EditText emiDescription;
    }

}
