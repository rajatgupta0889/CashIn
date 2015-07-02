package com.mantralabsglobal.cashin.ui.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mobsandgeeks.saripaar.AnnotationRule;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by pk on 6/21/2015.
 */
public class MonthIncomeView extends LinearLayout {

    private EditText et_month;
    private EditText et_amount;
    private ImageView iv_icon;
    private int month;
    private int year;

    public MonthIncomeView(Context context) {
        this(context, null);
    }

    public MonthIncomeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthIncomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MonthIncomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_view, 0, 0);
        try {
            // iv_icon.setImageResource(R.drawable.ic_work_address);

            Drawable drawable = ta.getDrawable(R.styleable.custom_view_field_icon);
            if (drawable != null)
                iv_icon.setBackground(drawable);

            et_month.setText(ta.getString(R.styleable.custom_view_income_month));

            et_amount.setText(ta.getString(R.styleable.custom_view_income_amount));


        } finally {
            ta.recycle();
        }


    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_income_view, this);

        month = Calendar.getInstance().get(Calendar.MONTH);
        year = Calendar.getInstance().get(Calendar.YEAR);

        loadViews();
    }

    private void loadViews() {
        iv_icon = (ImageView)findViewById(R.id.ac_icon);
        et_month = (EditText)findViewWithTag("incomeMonth");//  (EditText)findViewById(R.id.et_text);
        et_amount = (EditText)findViewWithTag("incomeAmount");

        et_month.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        MonthIncomeView.this.year = year;
                        MonthIncomeView.this.month = monthOfYear;

                        updateDateView(year, month);
                    }
                }, year, month, 1);
                dialog.show();
            }
        });
        updateDateView(year, month);
    }

    private void updateDateView(int year, int month) {
        et_month.setText( getMonthForInt(month , year));
    }


    public int getMonth()
    {
        return month;
    }

    public void setMonth(int value)
    {
        month = value;
        updateDateView(year, month);
    }

    String getMonthForInt(int num, int year) {
        String month = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month + " " + year;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int value)
    {
        year = value;
        updateDateView(year, month);
    }

    public void setAmount(CharSequence name) {
        et_amount.setText(name);
    }

    public Editable getAmount() {
        return et_amount.getText();
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putString("incomeMonth", et_month.getText().toString());
        bundle.putString("incomeAmount", et_amount.getText().toString());
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            et_month.setText(bundle.getString("incomeMonth"));
            et_amount.setText(bundle.getString("incomeAmount"));


            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }


}
