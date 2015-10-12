package com.mantralabsglobal.cashin.service;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.squareup.okhttp.Call;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

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

/**
 * Created by pk on 6/25/2015.
 */
public interface AvtarService {

    @Multipart
    @POST("/user/avatar")
    public void uploadAvtarImage(@Part("avatar") TypedFile file, Callback<AvtarImage> callback);

    @GET("/user/avatar")
    public void getAvtarImage(Callback<AvtarImage> callback);

    public static class AvtarUtil
    {
        public static void getAvtarImage(final Callback<AvtarImage> callback, final AvtarService service,final Activity activity)
        {
            service.getAvtarImage(new Callback<AvtarImage>(){

                @Override
                public void success(final AvtarImage avtarImage, Response response) {
                    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean imageExists = false;
                            try {
                                HttpURLConnection.setFollowRedirects(false);
                                HttpURLConnection con = (HttpURLConnection) new URL(avtarImage.getAvatar()).openConnection();
                                con.setRequestMethod("HEAD");
                                System.out.println(con.getResponseCode());
                                imageExists = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(imageExists)
                            {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.success(avtarImage, null);
                                    }
                                });
                            }
                            else{
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

    public static class AvtarImage
    {
        private String avatar;

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
            else if(avatar != null && avatar.length()>0)
            {
                return Uri.parse(avatar);
            }
            return null;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

}
