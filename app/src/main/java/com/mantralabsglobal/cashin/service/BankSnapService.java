package com.mantralabsglobal.cashin.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface BankSnapService {

    @Multipart
    @POST("/user/attachment")
    public void uploadBankSnapImage(@Part("bank_statement") TypedFile file, Callback<BankSnapImage> callback);

    @GET("/user/attachment")
    public void getBankStmtSnapImage(Callback<BankSnapImage> callback);

    public static class BankSnapUtil
    {
        public static void getBankStatamentSnapImage(final Callback<BankSnapImage> callback, final BankSnapService service,final Activity activity)
        {
            service.getBankStmtSnapImage(new Callback<BankSnapImage>() {

                @Override
                public void success(final BankSnapImage bankSnapImage, Response response) {
                    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean imageExists = false;

                            try {
                                HttpURLConnection.setFollowRedirects(false);
                                Log.d("BankSnapService", bankSnapImage.getBankImage());
                                HttpURLConnection con = (HttpURLConnection) new URL(bankSnapImage.getBankImage()).openConnection();
                                con.setRequestMethod("HEAD");
                                System.out.println(con.getResponseCode());
                                imageExists = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (imageExists) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.success(bankSnapImage, null);
                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.success(null, null);
                                    }
                                });
                            }

                            return null;
                        }
                    }.execute();
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failure(error);
                }
            });
        }
    }

    public static class BankSnapImage
    {
        private String bankImage;

        private String filePath;


        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Uri getImageUri()
        {
            if(filePath != null && filePath.length()>0)
            {
                return Uri.fromFile(new File(filePath));
            }
            else if(bankImage != null && bankImage.length()>0)
            {
                return Uri.parse(bankImage);
            }
            return null;
        }

        public String getBankImage() {
            return bankImage;
        }

        public void setBankImage(String bankImage) {
            this.bankImage = bankImage;
        }
    }


}
