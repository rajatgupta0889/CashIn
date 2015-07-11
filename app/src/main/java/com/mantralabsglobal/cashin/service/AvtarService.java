package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by pk on 6/25/2015.
 */
public interface AvtarService {

    @Multipart
    @POST("/user/aadhar")
    void uploadAvtarImage(@Part("avatar") TypedFile file, Callback<String> callback);


}
