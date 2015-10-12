package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @POST("/user/ocr/businessCard")
    void getBusinessCardDetailFromImage(@Body OCRServiceProvider.CardImage businessCardImage, Callback<BusinessCardDetail> callback);

    public static class BusinessCardDetail{

        @SerializedName("BusinessCardDetail")
        private List<String> contentArr;

        @SerializedName("Address")
        private List<String> addressLines;

        @SerializedName("Email")
        private String email;

        @SerializedName("CompanyName")
        private String employerName;

        @SerializedName("Name")
        private String name;

        public List<String> getaddressLines() {
            return addressLines;
        }

        public void setAddress(List<String> addressLines) {
            this.addressLines = addressLines;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getContentArr() {
            return contentArr;
        }

        public void setContentArr(List<String> contentArr) {
            this.contentArr = contentArr;
        }
    }

}
