package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface AddressService {

    @GET("/user/address/current")
    void getCurrentAddress(AuthenticationService.AuthenticatedUser user, Callback<Address> callback);

    @GET("/user/address/permanent")
    void getPermanentAddress(AuthenticationService.AuthenticatedUser user, Callback<Address> callback);

    @POST("/user/address/current")
    void setCurrentAddress(AuthenticationService.AuthenticatedUser user, Address address ,Callback<Address> callback);

    @POST("/user/address/permanent")
    void setPermanentAddress(AuthenticationService.AuthenticatedUser user, Address address, Callback<Address> callback);


    public static class Address{

        private String street;
        private String city;
        private String state;
        private String pincode;
        private boolean isHouseRented;
        private String own;

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
    }



}
