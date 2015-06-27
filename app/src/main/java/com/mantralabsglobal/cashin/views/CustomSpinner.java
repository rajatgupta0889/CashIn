package com.mantralabsglobal.cashin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 6/21/2015.
 */
public class CustomSpinner extends LinearLayout {

    private TextView tv_label;
    private Spinner spinner;
    private ImageView iv_icon;

    public CustomSpinner(Context context) {
        this(context, null);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_view, 0, 0);
        try {
            tv_label.setText(ta.getString(R.styleable.custom_view_field_label));

            Drawable drawable = ta.getDrawable(R.styleable.custom_view_field_icon);
            if (drawable != null)
                iv_icon.setBackground(drawable);

        } finally {
            ta.recycle();
        }


    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_spinner_view, this);


        loadViews();
    }

    private void loadViews() {
        iv_icon = (ImageView)findViewById(R.id.ac_icon);
        spinner = (Spinner)findViewById(R.id.spin_spinner);
        tv_label = (TextView)findViewById(R.id.tv_label);
    }

    public Spinner getSpinner()
    {
        return spinner;
    }

    public void setAdapter(SpinnerAdapter adapter)
    {
        spinner.setAdapter(adapter);
    }

    public SpinnerAdapter getAdapter()
    {
        return spinner.getAdapter();
    }

    public CharSequence getLabel()
    {
        return tv_label.getText();
    }

    public void setLabel(CharSequence value)
    {
         tv_label.setText(value);
    }

    public void setSelection(int position) {
        spinner.setSelection(position);
    }
}
