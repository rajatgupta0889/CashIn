package com.mantralabsglobal.cashin.utils;

import com.google.gson.Gson;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * Created by pk on 7/3/2015.
 */
public class RetrofitUtils {

    public static String USER_NOT_REGISTERED_ERROR =  "Given email does not exist";

    public static boolean isUserNotRegisteredError(RetrofitError error)
    {
        return USER_NOT_REGISTERED_ERROR.equalsIgnoreCase(getErrorMessage(error).message);
    }

    public static ErrorMessage getErrorMessage(RetrofitError error)
    {
        String json = new String( ((TypedByteArray)error.getResponse().getBody()).getBytes());
        Gson gson = new Gson();
        return gson.fromJson(json, ErrorMessage.class);
    }

    public static class ErrorMessage {
        public String getMessage() {
            return message;
        }

        private String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
