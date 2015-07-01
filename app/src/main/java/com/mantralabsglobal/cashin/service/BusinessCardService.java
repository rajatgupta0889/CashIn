package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface BusinessCardService {

    @GET("/user/businessCard")
    void getBusinessCardDetail(Callback<BusinessCardDetail> callback);

    @POST("/user/businessCard")
    void createBusinessCardDetail(@Body BusinessCardDetail businessCardDetail, Callback<BusinessCardDetail> callback);

    @PUT("/user/businessCard")
    void updateBusinessCardDetail(@Body BusinessCardDetail businessCardDetail, Callback<BusinessCardDetail> callback);


    public static class BusinessCardDetail{

        private String address;
        private String email;

        @SerializedName("name")
        private String employerName;


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmployerName() {
            return employerName;
        }

        public void setEmployerName(String employerName) {
            this.employerName = employerName;
        }
    }

}
