package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.businessobjects.AadharDetail;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface AadharService {

    @GET("/user/aadhar")
    void getAadharDetail(AuthenticationService.AuthenticatedUser user, Callback<List<AadharDetail>> callback);

    @POST("/user/aadhar")
    void setAadharDetail(AuthenticationService.AuthenticatedUser user, AadharDetail aadhar, Callback<List<AadharDetail>> callback);

    public static class AadharDetail{

        private String name;
        private String address;
        private String gender;
        @SerializedName(value="aadhar_no")
        private String aadharNumber;
        @SerializedName(value = "sonOf")
        private String sonOf;
        private String dob;
    }

}
