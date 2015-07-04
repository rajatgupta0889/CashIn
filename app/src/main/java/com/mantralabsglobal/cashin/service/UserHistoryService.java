package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 7/4/2015.
 */
public interface UserHistoryService {

    @GET("/user/history")
    void getUserHistory( Callback<UserHistory> callback);

    @POST("/user/history")
    void createUserHistory(@Body UserHistory userHistory, Callback<UserHistory> callback);

    @PUT("/user/history")
    void updateUserHistory(@Body UserHistory userHistory, Callback<UserHistory> callback);

    public static class UserHistory {

        private boolean loanTaken;
        @SerializedName("isCheckBounced")
        private boolean isChequeBounced;
        private boolean applicationRejected;
        @SerializedName("isDefaulted")
        private boolean hasDefaulted;

        public boolean isLoanTaken() {
            return loanTaken;
        }

        public void setLoanTaken(boolean loanTaken) {
            this.loanTaken = loanTaken;
        }

        public boolean isChequeBounced() {
            return isChequeBounced;
        }

        public void setIsChequeBounced(boolean isChequeBounced) {
            this.isChequeBounced = isChequeBounced;
        }

        public boolean isApplicationRejected() {
            return applicationRejected;
        }

        public void setApplicationRejected(boolean applicationRejected) {
            this.applicationRejected = applicationRejected;
        }

        public boolean isHasDefaulted() {
            return hasDefaulted;
        }

        public void setHasDefaulted(boolean hasDefaulted) {
            this.hasDefaulted = hasDefaulted;
        }
    }
}
