package com.mantralabsglobal.cashin.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by pk on 7/5/2015.
 */
public class DateUtils {
    public static long computeAge(int year, int monthOfYear, int dayOfMonth, TimeUnit timeUnit)
    {
        Calendar givenDate = Calendar.getInstance();
        givenDate.set(year, monthOfYear, dayOfMonth);
        Calendar now = Calendar.getInstance();
        Calendar clone = (Calendar) givenDate.clone(); // Otherwise changes are been reflected.
        int years = -1;
        long diff = now.getTimeInMillis() - clone.getTimeInMillis();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getYearsPassed(int year, int monthOfYear, int dayOfMonth) {
        Calendar myBirthDate = Calendar.getInstance();
        myBirthDate.clear();
        myBirthDate.set(year, monthOfYear, dayOfMonth);
        Calendar now = Calendar.getInstance();
        Calendar clone = (Calendar) myBirthDate.clone(); // Otherwise changes are been reflected.
        int years = -1;
        while (!clone.after(now)) {
            clone.add(Calendar.YEAR, 1);
            years++;
        }
        return years;
    }
}
