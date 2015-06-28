package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;

import com.mantralabsglobal.cashin.service.AddressService;

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


    public void getCurrentAddress(final Context context, final AddressListener listener) {
        AppLocationService appLocationService = new AppLocationService(context);
        Message message = Message.obtain();
        final Location location = getLocation(appLocationService);

        Thread t = new Thread()
        {
            public void run()
            {
                Address address = null;
                try {
                    address = getAddressFromLocation(location.getLatitude(), location.getLongitude(), context);
                } catch (IOException e) {
                    listener.onError(e);
                    return;
                }

                AddressService.Address myAddress = new AddressService.Address();
                myAddress.setCity(address.getSubAdminArea());
                myAddress.setState(address.getAdminArea());
                myAddress.setPincode(address.getPostalCode());

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {

                    String addressLne =  address.getAddressLine(i);
                    if( addressLne != null && addressLne.length()>0 && addressLne.indexOf(myAddress.getCity())>=0)
                        break;
                    else {
                        if(sb.length()>0)
                            sb.append(", ");
                        sb.append(addressLne);
                    }
                }

                myAddress.setStreet(sb.toString());

                listener.onAddressAquired(myAddress);
            }
        };

        t.start();

    }

    private Location getLocation(AppLocationService appLocationService)
    {
        Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        if(location == null)
            location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }
    private Address getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context) throws IOException {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Bundle bundle = new Bundle();
        Address address = null;

        List<Address> addressList = geocoder.getFromLocation(
                latitude, longitude, 1);
        if (addressList != null && addressList.size() > 0)
            address = addressList.get(0);

       return address;

    }

    public interface AddressListener{
        public void onAddressAquired(AddressService.Address address);
        public void onError(Throwable error);
    }

}