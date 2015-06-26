package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface IncomeService {

    @GET("/user/income")
    void getIncomeDetail(AuthenticationService.AuthenticatedUser user, Callback<List<Income>> callback);

    @POST("/user/income")
    void setIncome(AuthenticationService.AuthenticatedUser user, List<Income> bankDetail, Callback<List<Income>> callback);

    public static class Income{

        int year;
        int month;
        double amount;

    }

}
