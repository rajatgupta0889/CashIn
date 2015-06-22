package com.mantralabsglobal.cashin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
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
public class SonOfSpinner extends LinearLayout {

    private Spinner spinner;
    private ImageView iv_icon;
    private TextView et_text;
    private ArrayAdapter<CharSequence> relationAdapter;


    public SonOfSpinner(Context context) {
        this(context, null);
    }

    public SonOfSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SonOfSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SonOfSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_view, 0, 0);
        try {
           // iv_icon.setImageResource(R.drawable.ic_work_address);

            Drawable drawable = ta.getDrawable(R.styleable.custom_view_field_icon);
            if (drawable != null)
                iv_icon.setBackground(drawable);

        } finally {
            ta.recycle();
        }


    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.son_of_view, this);


        loadViews();
    }

    private void loadViews() {
        iv_icon = (ImageView)findViewById(R.id.ac_icon);
        spinner = (Spinner)findViewById(R.id.spin_sonof);
        et_text = (EditText)findViewById(R.id.et_sonof);
        if(!isInEditMode())
            setAdapter(getRelationAdapter());
    }

    public void setAdapter(SpinnerAdapter adapter)
    {
        spinner.setAdapter(adapter);
    }

    public ArrayAdapter<CharSequence> getRelationAdapter()
    {
        if(relationAdapter == null)
        {
            relationAdapter = ArrayAdapter.createFromResource(getContext(), R.array.relationship_array, android.R.layout.simple_spinner_item);
            relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return relationAdapter;
    }

    public SpinnerAdapter getAdapter()
    {
        return spinner.getAdapter();
    }

    public void setSelection(int position) {
        spinner.setSelection(position);
    }

    public void setText(CharSequence name) {
        et_text.setText(name);
    }
}
