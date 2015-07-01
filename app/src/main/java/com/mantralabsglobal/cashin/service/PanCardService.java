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
public interface PanCardService {

    @GET("/user/pancard")
    void getPanCardDetail(Callback<PanCardDetail> callback);

    @POST("/user/pancard")
    void createPanCardDetail(@Body PanCardDetail panCardDetail, Callback<PanCardDetail> callback);

    @PUT("/user/pancard")
    void updatePanCardDetail(@Body PanCardDetail panCardDetail, Callback<PanCardDetail> callback);


    public static class PanCardDetail{

        private String name;
        @SerializedName(value="pan_no")
        private String panNumber;
        @SerializedName(value = "sonOf")
        private String sonOf;
        private String dob;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSonOf() {
            return sonOf;
        }

        public void setSonOf(String sonOf) {
            this.sonOf = sonOf;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getPanNumber() {
            return panNumber;
        }

        public void setPanNumber(String panNumber) {
            this.panNumber = panNumber;
        }
    }

}
