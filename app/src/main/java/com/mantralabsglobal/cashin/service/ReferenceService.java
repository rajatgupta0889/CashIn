package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface ReferenceService {

    @GET("/user/referenceDetail")
    void getReferences(Callback<List<Reference>> callback);

    @POST("/user/addReference")
    void addReferences(@Body List<Reference> bankDetail, Callback<List<Reference>> callback);

    @POST("/user/editReference")
    void updateReferences(@Body List<Reference> bankDetail, Callback<List<Reference>> callback);

    public static class Reference implements Serializable{

        @SerializedName("refName")
        private String name;
        @SerializedName("refMobile")
        private String number;
        @SerializedName("relationship")
        private String relationship;
        @SerializedName("refEmail")
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
