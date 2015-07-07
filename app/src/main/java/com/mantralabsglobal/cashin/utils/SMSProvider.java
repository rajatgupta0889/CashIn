package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pk on 7/2/2015.
 */
public class SMSProvider {

    private static final String TAG = "SMSProvider";

    private static final String HDFC = "HDFC";

    private static final String ICICI = "ICICI";

    public List<SMSMessage> readSMS(Context context, Predicate<SMSMessage> filter)
    {
        // public static final String INBOX = "content://sms/inbox";
// public static final String SENT = "content://sms/sent";
// public static final String DRAFT = "content://sms/draft";
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        List<SMSMessage> smsList = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                //String msgData = "";
                SMSMessage message = new SMSMessage();
                message.setBody(cursor.getString(cursor.getColumnIndex("body")));
                message.setSubject(cursor.getString(cursor.getColumnIndex("subject")));
                message.setType(cursor.getString(cursor.getColumnIndex("type")));
                message.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                message.setDate(cursor.getString(cursor.getColumnIndex("date")));
                message.setId(cursor.getString(cursor.getColumnIndex("_id")));
                message.setLocked(cursor.getString(cursor.getColumnIndex("locked")));
                message.setPerson(cursor.getString(cursor.getColumnIndex("person")));
                message.setProtocol(cursor.getString(cursor.getColumnIndex("protocol")));
                message.setRead(cursor.getString(cursor.getColumnIndex("read")));
                message.setReply_path_present(cursor.getString(cursor.getColumnIndex("reply_path_present")));
                message.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                message.setThread_id(cursor.getString(cursor.getColumnIndex("thread_id")));
                message.setService_center(cursor.getString(cursor.getColumnIndex("service_center")));

                if(filter.apply(message))
                    smsList.add(message);

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        return smsList;
    }

    public boolean isSenderBank(SMSMessage message)
    {
        return getBankName(message) != null;
    }

    public boolean hasAccountInformation(SMSMessage message)
    {
        return getAccountNumber(message) != null;
    }

    public String getAccountNumber(SMSMessage message)
    {
        if(HDFC.equals(getBankName(message))) {
            int startIndex = message.getBody().indexOf("A/c");
            startIndex = message.getBody().indexOf("XX", startIndex);
            int endIndex = message.getBody().indexOf(" ", startIndex);
            if(startIndex>=0 && endIndex>=0)
                return message.getBody().substring(startIndex, endIndex);
        }
        else if(ICICI.equals(getBankName(message)))
        {
            int startIndex = message.getBody().indexOf("Your Ac");
            startIndex = message.getBody().indexOf("XX", startIndex);
            int endIndex = message.getBody().indexOf(" ", startIndex );
            if(startIndex>=0 && endIndex>=0)
                return message.getBody().substring(startIndex, endIndex);

        }
        return null;
    }

    public String getBankName(SMSMessage message)
    {
        if(message.getAddress().indexOf(HDFC)>=0) {
           return HDFC;
        }
        else if(message.getAddress().indexOf(ICICI)>=0 || message.getBody().indexOf(ICICI)>=0) {
            return ICICI;
        }
        return null;
    }

    public static class SMSMessage
    {
        private String id;
        private String thread_id;
        private String address;
        private String person;
        private String date;
        private String protocol;
        private String read;
        private String status;
        private String type;
        private String reply_path_present;
        private String subject;
        private String body;
        private String service_center;
        private String locked;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getThread_id() {
            return thread_id;
        }

        public void setThread_id(String thread_id) {
            this.thread_id = thread_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReply_path_present() {
            return reply_path_present;
        }

        public void setReply_path_present(String reply_path_present) {
            this.reply_path_present = reply_path_present;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getService_center() {
            return service_center;
        }

        public void setService_center(String service_center) {
            this.service_center = service_center;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }
    }
}
