package com.mantralabsglobal.cashin.service;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by pk on 7/15/2015.
 */
public interface OCRServiceProvider<T> {

    void getDetailFromImage(CardImage image, Callback<T> callback);

    public static class CardImage
    {
        private String base64encodedImage;

        public String getBase64encodedImage() {
            return base64encodedImage;
        }

        public void setBase64encodedImage(String base64encodedImage) {
            this.base64encodedImage = base64encodedImage;
        }
    }
}
