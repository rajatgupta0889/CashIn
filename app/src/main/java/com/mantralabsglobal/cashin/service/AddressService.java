package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface AddressService {

    @GET("/user/address")
    void getAddress(Callback<List<Address>> callback);

    @POST("/user/address")
    void setAddress(@Body Address address ,Callback<Address> callback);

    public static class Address{

        private String street;
        private String city;
        private String state;
        private String pincode;
        private boolean isHouseRented;
        private String own;
        @SerializedName("user")
        private String userId;
        private String type;
        private Date createdAt;
        private Date updatedAt;
        @SerializedName("id")
        private String addressId;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public boolean isHouseRented() {
            return isHouseRented;
        }

        public void setIsHouseRented(boolean isHouseRented) {
            this.isHouseRented = isHouseRented;
        }

        public String getOwn() {
            return own;
        }

        public void setOwn(String own) {
            this.own = own;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getAddressId() {
            return addressId;
        }
    }


}
