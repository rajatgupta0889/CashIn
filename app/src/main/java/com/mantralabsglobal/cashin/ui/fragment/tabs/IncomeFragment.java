package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.IncomeService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.MonthIncomeView;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by pk on 14/06/2015.
 */
public class IncomeFragment extends BaseBindableFragment<List<IncomeService.Income>> {

    @NotEmpty
    @Digits
    @InjectView(R.id.cc_month_one)
    public MonthIncomeView monthIncomeViewOne;

    @Digits
    @NotEmpty
    @InjectView(R.id.cc_month_two)
    public MonthIncomeView monthIncomeViewTwo;

    @Digits
    @NotEmpty
    @InjectView(R.id.cc_month_three)
    public MonthIncomeView monthIncomeViewThree;


    IncomeService incomeService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        incomeService = ((Application)getActivity().getApplication()).getRestClient().getIncomeService();

        reset(false);

    }

    @Override
    protected void onUpdate(List<IncomeService.Income> updatedData, Callback<List<IncomeService.Income>> saveCallback) {
        onCreate(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(List<IncomeService.Income> updatedData, Callback<List<IncomeService.Income>> saveCallback) {
        incomeService.createIncome(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<List<IncomeService.Income>> dataCallback) {
        incomeService.getIncomeDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);

        monthIncomeViewOne.setMonth(cal.get(Calendar.MONTH));
        monthIncomeViewOne.setYear(cal.get(Calendar.YEAR));

        cal.add(Calendar.MONTH, -1);
        monthIncomeViewTwo.setMonth(cal.get(Calendar.MONTH));
        monthIncomeViewTwo.setYear(cal.get(Calendar.YEAR));

        cal.add(Calendar.MONTH, -1);

        monthIncomeViewThree.setMonth(cal.get(Calendar.MONTH));
        monthIncomeViewThree.setYear(cal.get(Calendar.YEAR));

    }



    @Override
    public void bindDataToForm(List<IncomeService.Income> value) {
        if(value.size()>0)
        {
            monthIncomeViewOne.setYear(value.get(0).getYear());
            monthIncomeViewOne.setMonth(value.get(0).getMonth()-1);
            monthIncomeViewOne.setAmount(String.valueOf(value.get(0).getAmount()));

            if(value.size()==2)
            {
                monthIncomeViewTwo.setYear(value.get(1).getYear());
                monthIncomeViewTwo.setMonth(value.get(1).getMonth()-1);
                monthIncomeViewTwo.setAmount(String.valueOf(value.get(1).getAmount()));
            }
            if(value.size()==3)
            {
                monthIncomeViewThree.setYear(value.get(2).getYear());
                monthIncomeViewThree.setMonth(value.get(2).getMonth()-1);
                monthIncomeViewThree.setAmount(String.valueOf(value.get(2).getAmount()));
            }
        }
    }

    @Override
    public List<IncomeService.Income> getDataFromForm(List<IncomeService.Income> base) {
        if(base == null) {
            base = new ArrayList<>();
        }
        while(base.size()<3)
        {
            base.add(new IncomeService.Income());
        }
        base.get(0).setYear(monthIncomeViewOne.getYear());
        base.get(0).setMonth(monthIncomeViewOne.getMonth() + 1);
        base.get(0).setAmount( Double.parseDouble(monthIncomeViewOne.getAmount().toString()));

        base.get(1).setYear(monthIncomeViewTwo.getYear());
        base.get(1).setMonth(monthIncomeViewTwo.getMonth() + 1);
        base.get(1).setAmount(Double.parseDouble(monthIncomeViewTwo.getAmount().toString()));

        base.get(2).setYear(monthIncomeViewThree.getYear());
        base.get(2).setMonth(monthIncomeViewThree.getMonth() + 1);
        base.get(2).setAmount( Double.parseDouble(monthIncomeViewThree.getAmount().toString()));

        return base;
    }
}
