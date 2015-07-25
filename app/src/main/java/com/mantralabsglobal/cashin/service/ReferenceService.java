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

    @GET("/user/references")
    void getReferences(Callback<List<Reference>> callback);

    @POST("/user/references")
    void addReferences(@Body List<Reference> bankDetail, Callback<List<Reference>> callback);

    public static class Reference implements Serializable{

        private String name;
        private String number;
        private String relationship;

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
    }

}
