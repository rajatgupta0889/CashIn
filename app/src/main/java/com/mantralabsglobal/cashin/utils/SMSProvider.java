package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by pk on 7/2/2015.
 */
public class SMSProvider {

    protected void readSMS(Context context)
    {
        // public static final String INBOX = "content://sms/inbox";
// public static final String SENT = "content://sms/sent";
// public static final String DRAFT = "content://sms/draft";
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }
}
