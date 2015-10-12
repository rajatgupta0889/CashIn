package com.mantralabsglobal.cashin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface EMIService {

    @GET("/user/emi")
    void getEMIDetail( Callback<List<EMI>> callback);

    @POST("/user/emi")
    void createEMI(@Body List<EMI> emiDetail, Callback<List<EMI>> callback);

    public static class EMI{

        int emiAmount ;
        String EMIDescription ;

        public EMI() {
        }

        public int getEmiAmount() {
            return emiAmount;
        }

        public void setEmiAmount(int emiAmount) {
            this.emiAmount = emiAmount;
        }

        public String getEMIDescription() {
            return EMIDescription;
        }

        public void setEMIDescription(String EMIDescription) {
            this.EMIDescription = EMIDescription;
        }
    }
}
