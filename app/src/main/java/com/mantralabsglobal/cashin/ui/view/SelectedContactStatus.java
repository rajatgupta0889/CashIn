package com.mantralabsglobal.cashin.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;

import java.util.HashMap;

/**
 * Created by hello on 7/22/2015.
 */
public class SelectedContactStatus extends LinearLayout {

    private int maxSelectionount;
    private int currentSelectionCount;

    public SelectedContactStatus(Context context) {
        this(context,null);
    }

    public SelectedContactStatus(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedContactStatus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_select_contact_status, this);

        TextView selectionCount = (TextView) findViewById(R.id.text_selection_count);
        selectionCount.setText(currentSelectionCount +"/" + maxSelectionount);
    }

    public int getMaxSelectionount() {
        return maxSelectionount;
    }

    public void setMaxSelectionount(int maxSelectionount) {
        this.maxSelectionount = maxSelectionount;
        TextView selectionCount = (TextView) findViewById(R.id.text_selection_count);
        selectionCount.setText(currentSelectionCount +"/" + maxSelectionount);
    }

    public int getCurrentSelectionCount() {
        return currentSelectionCount;
    }

    public void setCurrentSelectionCount(int currentSelectionCount) {
        this.currentSelectionCount = currentSelectionCount;
        TextView selectionCount = (TextView) findViewById(R.id.text_selection_count);
        selectionCount.setText(currentSelectionCount +"/" + maxSelectionount);
    }
}
