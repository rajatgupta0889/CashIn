package com.mantralabsglobal.cashin.social;

/**
 * Created by pk on 7/4/2015.
 */
public class SocialBase {

    public static interface SocialListener<T>
    {
        void onSuccess(T t);
        void onFailure(String message);
    }
}
