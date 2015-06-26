package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface FacebookService {

    @GET("/user/fbrofile")
    void getFacebookProfile(Callback<FacebookProfile> callback);

    @POST("/user/fbrofile")
    void setFacebokProfile(FacebookProfile linkedInDetail, Callback<FacebookProfile> callback);

    public static class FacebookProfile{

        private String workspace;
        private String city;
        private String hometown;
        private String relationshipStatus;
        private String dob;

        public String getWorkspace() {
            return workspace;
        }

        public void setWorkspace(String workspace) {
            this.workspace = workspace;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getHometown() {
            return hometown;
        }

        public void setHometown(String hometown) {
            this.hometown = hometown;
        }

        public String getRelationshipStatus() {
            return relationshipStatus;
        }

        public void setRelationshipStatus(String relationshipStatus) {
            this.relationshipStatus = relationshipStatus;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }
    }


}
