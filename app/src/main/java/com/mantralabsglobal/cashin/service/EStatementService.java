package com.mantralabsglobal.cashin.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface EStatementService {

    @GET("/user/statementperfios")
    void getEStatement(Callback<EStatement> callback);

    @POST("/user/statementperfios")
    void createEStatement(@Body EStatement emiDetail, Callback<EStatement> callback);

    public static class EStatement{

        int status = -1;
        String message ;

        public EStatement() {
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
