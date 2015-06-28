package com.mantralabsglobal.cashin.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;

/**
 * Created by pk on 6/21/2015.
 */
public class CustomEditText extends LinearLayout  {

    private TextView tv_label;
    private EditText et_editText;
    private ImageView iv_icon;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_view, 0, 0);
        try {
            tv_label.setText(ta.getString(R.styleable.custom_view_field_label));
           // iv_icon.setImageResource(R.drawable.ic_work_address);

            Drawable drawable = ta.getDrawable(R.styleable.custom_view_field_icon);
            if (drawable != null)
                iv_icon.setBackground(drawable);
            et_editText.setText(ta.getString(R.styleable.custom_view_field_text));

            boolean isPassword = ta.getBoolean(R.styleable.custom_view_password, false);
            if(isPassword)
                et_editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } finally {
            ta.recycle();
        }


    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_edittext_view, this);


        loadViews();
    }

    private void loadViews() {
        iv_icon = (ImageView)findViewById(R.id.ac_icon);
        et_editText = (EditText)findViewById(R.id.et_text);
        tv_label = (TextView)findViewById(R.id.tv_label);
    }

    public CharSequence getLabel()
    {
        return tv_label.getText();
    }

    public void setLabel(CharSequence value)
    {
         tv_label.setText(value);
    }

    public void setText(CharSequence name) {
        et_editText.setText(name);
    }

    public Editable getText() {
        return et_editText.getText();
    }

    public EditText getEditText()
    {
        return et_editText;
    }

}
