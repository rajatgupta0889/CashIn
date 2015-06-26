package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface PrimaryBankService {

    @GET("/user/bank")
    void getPrimaryBankDetail(AuthenticationService.AuthenticatedUser user, Callback<BankDetail> callback);

    @POST("/user/bank")
    void setPrimaryBankDetail(AuthenticationService.AuthenticatedUser user, BankDetail bankDetail,  Callback<BankDetail> callback);

    public static class BankDetail{

        @SerializedName(value = "name")
        private String bankName;
        @SerializedName(value = "account_no")
        private String accountNumber;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

}
