package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.android.internal.util.Predicate;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.BankProvider;
import com.mantralabsglobal.cashin.service.PrimaryBankService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pk on 7/2/2015.
 */
public class SMSProvider {

    private static final String TAG = "SMSProvider";

    private String [] banks;
    private String [] debitKeywords;
    private String [] creditKeywords;
    private String [] loanKeywords;
    private String [] billKeywords;
    private String [] accountKeywords;
    private String [] currencyKeywords;
    private Context context;
    private Pattern accountNumberPattern;
    private Pattern bankNamePattern;

    public SMSProvider(Context context)
    {
        this.context = context;
        banks = BankProvider.getInstance().getBanks().getBankCodeList().toArray(new String [] {});
        debitKeywords = context.getResources().getStringArray(R.array.debit_keywords);
        creditKeywords = context.getResources().getStringArray(R.array.credit_keywords);
        loanKeywords = context.getResources().getStringArray(R.array.loan_keywords);
        billKeywords = context.getResources().getStringArray(R.array.bill_keywords);
        accountKeywords = context.getResources().getStringArray(R.array.account_keyword);
        currencyKeywords = context.getResources().getStringArray(R.array.currency_keywords);
        accountNumberPattern = Pattern.compile(context.getString(R.string.account_name_regex));
        bankNamePattern = Pattern.compile(context.getString(R.string.bank_name_regex));
    }

    public static class ReadBankAccountInfoTask extends AsyncTask<Long, String,  List<PrimaryBankService.BankDetail>>{

        private final Predicate<SMSMessage> filter;
        private final SMSProvider provider;

        public ReadBankAccountInfoTask(Predicate<SMSMessage> filter, SMSProvider provider)
        {
            this.filter = filter;
            this.provider = provider;
        }

        @Override
        protected  List<PrimaryBankService.BankDetail> doInBackground(Long... params) {
            Cursor cursor = provider.getSMSInboxCursor();
            Long since = params[0];
            int maxCount = cursor.getCount();
            List<PrimaryBankService.BankDetail> bankDetailList = new ArrayList<>();
            Map<String, Integer> bankCount = new HashMap<>();
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                int index = 1;
                do {
                    publishProgress(String.format("Processing inbox %d/%d. Found %d.", index, maxCount, bankDetailList.size() ));

                    if(cursor.getLong(cursor.getColumnIndex("date"))<since)
                        break;
                    //String msgData = "";
                    SMSMessage message = provider.mapCursor(cursor);

                    if(filter.apply(message))
                    {
                        PrimaryBankService.BankDetail bankDetail = new PrimaryBankService.BankDetail();
                        bankDetail.setAccountNumber(provider.getAccountNumber(message));
                        bankDetail.setBankName(provider.getBankName(message));
                        if (!bankDetailList.contains(bankDetail))
                            bankDetailList.add(bankDetail);
                        if (!bankCount.containsKey(bankDetail.getAccountNumberLast4Digits()))
                            bankCount.put(bankDetail.getAccountNumberLast4Digits(), 0);
                        int newCount = bankCount.get(bankDetail.getAccountNumberLast4Digits()) + 1;
                        bankCount.put(bankDetail.getAccountNumberLast4Digits(), newCount);
                    }

                    index++;

                } while (cursor.moveToNext());
            } else {
                publishProgress(String.format("Inbox Empty" ));
            }
            return bankDetailList;
        }
    }

    public List<SMSMessage> readSMS(Predicate<SMSMessage> filter)
    {
        return readSMS(filter, Long.MIN_VALUE);
    }

    protected Cursor getSMSInboxCursor() {
        return context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
    }

    protected SMSMessage mapCursor(Cursor cursor)
    {
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
        return message;
    }

    public List<SMSMessage> readSMS( Predicate<SMSMessage> filter, long since)
    {
        Cursor cursor = getSMSInboxCursor();

        List<SMSMessage> smsList = new ArrayList<>();

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                if(cursor.getLong(cursor.getColumnIndex("date"))<since)
                    break;
                //String msgData = "";
                SMSMessage message = mapCursor(cursor);

                if(filter.apply(message))
                    smsList.add(message);

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        return smsList;
    }

    public List<SMSMessage> getTransactionList(long since)
    {
        return readSMS(new Predicate<SMSMessage>() {
            @Override
            public boolean apply(SMSMessage message) {
                return isTransactionMessage(message);
            }
        }, since);
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
        String bankName = getBankName(message);
        if(bankName!= null && bankName.length()>=0)
        {
            Matcher m = accountNumberPattern.matcher(message.getBody());
            while (m.find()) {
                return "XX" + m.group(1);
            }
        }
        return null;
    }

    public String getBankName(SMSMessage message)
    {
        Matcher m = bankNamePattern.matcher(message.getAddress());
        while (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public boolean isTransactionMessage(SMSMessage message)
    {
        int sum = 0;
        for(String currency: currencyKeywords)
            sum += message.getBody().toLowerCase().indexOf(currency.toLowerCase())>1?2:0;
        for(String debit: debitKeywords)
            sum += message.getBody().toLowerCase().indexOf(debit.toLowerCase())>1?1:0;
        for(String account: accountKeywords)
            sum += message.getBody().toLowerCase().indexOf(account.toLowerCase())>1?1:0;
        for(String credit: creditKeywords)
            sum += message.getBody().toLowerCase().indexOf(credit.toLowerCase())>1?1:0;
        for(String loan: loanKeywords)
            sum += message.getBody().toLowerCase().indexOf(loan.toLowerCase())>1?1:0;
        for(String bill: billKeywords)
            sum += message.getBody().toLowerCase().indexOf(bill.toLowerCase())>1?1:0;

        return sum >2;
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
