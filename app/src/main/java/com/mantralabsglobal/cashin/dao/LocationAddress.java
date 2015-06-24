package com.mantralabsglobal.cashin.dao;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by pk on 6/24/2015.
 */
public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static final String STREET = "STREET";
    public static final String CITY = "CITY";
    public static final String STATE = "STATE";
    public static final String PINCODE = "PINCODE";
    public static final String ERROR = "ERROR";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Bundle bundle = new Bundle();
                Message message = Message.obtain();
                message.setTarget(handler);
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                       // sb.append(address.getLocality()).append("\n");
                        bundle.putString(STREET, sb.toString());
                        bundle.putString(STATE, address.getAdminArea());
                        bundle.putString(CITY, address.getSubAdminArea());
                        bundle.putString(PINCODE, address.getPostalCode());
                        message.what = 1;
                        message.setData(bundle);
                    }
                    else
                    {
                        message.what = 0;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    message.what = -1;
                } finally {

                   if (message.what != 1) {
                        String result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString(ERROR, result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}