package com.mantralabsglobal.cashin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 6/22/2015.
 */
public class BirthDayView extends LinearLayout {

    private EditText et_dob;
    private TextView tv_age;
    private TextView tv_dob;

    public BirthDayView(Context context) {
        this(context, null);
    }

    public BirthDayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BirthDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BirthDayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_edit_view, 0, 0);
        try {
            tv_dob.setText(ta.getString(R.styleable.custom_edit_view_field_label));
            // iv_icon.setImageResource(R.drawable.ic_work_address);


            et_dob.setText(ta.getString(R.styleable.custom_edit_view_field_text));
        } finally {
            ta.recycle();
        }


    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.birthday_field_view, this);


        loadViews();
    }

    private void loadViews() {
        et_dob = (EditText)findViewById(R.id.et_dob);
        tv_age = (TextView)findViewById(R.id.tv_age);
        tv_dob =  (TextView)findViewById(R.id.tv_dob);

    }
}
