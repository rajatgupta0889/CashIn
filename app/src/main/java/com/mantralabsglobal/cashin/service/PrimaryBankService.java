package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface PrimaryBankService {

    @GET("/user/bank")
    void getPrimaryBankDetail(Callback<List<BankDetail>> callback);

    @POST("/user/bank")
    void createPrimaryBankDetail(@Body List<BankDetail> bankDetail,  Callback<List<BankDetail>> callback);

    @POST("/user/netBankingperfios")
    void uploadPerfiosTransactionStatus(@Body PerfiosService.TransactionStatusResponse perfiosTransactionResponse,  Callback<PerfiosTransactionResponse> callback);

    @POST("/user/netBankingperfios")
    PerfiosTransactionResponse uploadPerfiosTransactionStatus(@Body PerfiosService.TransactionStatusResponse perfiosTransactionResponse);
    //@PUT("/user/bank")
    //void updatePrimaryBankDetail(@Body BankDetail bankDetail,  Callback<BankDetail> callback);


    public static class PerfiosTransactionResponse{
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class BankDetail{

        @SerializedName(value = "name")
        private String bankName;
        @SerializedName(value = "account_no")
        private String accountNumber;

        private boolean isPrimary;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getAccountNumberLast4Digits() {
            if(accountNumber!= null && accountNumber.length()>4)
            {
                return accountNumber.substring(accountNumber.length()-4);
            }
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj != null && obj instanceof BankDetail)
            {
                BankDetail second = (BankDetail)obj;
                return this.bankName.equals(second.getBankName())
                        && this.getAccountNumberLast4Digits().equals(second.getAccountNumberLast4Digits());
            }
            return false;
        }
    }

}
