package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface IncomeService {

    @GET("/user/income")
    void getIncomeDetail( Callback<List<Income>> callback);

    @POST("/user/income")
    void createIncome(@Body List<Income> bankDetail, Callback<List<Income>> callback);

    public static class Income{

        @SerializedName("Year")
        private int year;
        @SerializedName("Month")
        private int month;
        @SerializedName("Amount")
        private double amount;
        private String id;

        public Income() {
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
