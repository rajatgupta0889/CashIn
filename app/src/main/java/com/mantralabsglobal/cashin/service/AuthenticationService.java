package com.mantralabsglobal.cashin.service;

import java.util.Date;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface AuthenticationService {

    public final String USER_EMAIL= "USER_EMAIL";
    public final String USER_ID= "USER_ID";

    @POST("/login")
    void authenticateUser(@Body UserPrincipal userPrincipal  , Callback<AuthenticatedUser> callback);

    @POST("/signup")
    void registerUser(@Body NewUser user  , Callback<AuthenticatedUser> callback);

    public static class UserPrincipal{

        private String email;
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class AuthenticatedUser{
        private String message;
        private String email;
        private Date createdAt;
        private Date updatedAt;
        private String id;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class NewUser{
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }
}
