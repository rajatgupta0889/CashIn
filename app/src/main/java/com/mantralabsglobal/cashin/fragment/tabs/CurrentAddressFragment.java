package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.dao.AppLocationService;
import com.mantralabsglobal.cashin.dao.LocationAddress;
import com.mantralabsglobal.cashin.views.CustomEditText;
import com.mantralabsglobal.cashin.views.CustomSpinner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by pk on 13/06/2015.
 */
public class CurrentAddressFragment extends BaseFragment {

    CustomEditText cc_street;
    CustomEditText cc_pincode;
    CustomEditText cc_city;
    CustomEditText cc_state;
    ImageButton ib_get_gps_location;
    AppLocationService appLocationService;
    View addressForm;
    View gpsForm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
        appLocationService = new AppLocationService(getActivity());
        Button btnEdit = (Button) view.findViewById(R.id.editCurrentAddressButton);
        btnEdit.setOnClickListener(listener);

        FloatingActionButton btnGps = (FloatingActionButton) view.findViewById(R.id.gpsLocationFormButton);
        btnGps.setOnClickListener(listener);

        /*CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_city);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.city_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        /*CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_state);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        cc_street = (CustomEditText)view.findViewById(R.id.cc_street);
        cc_pincode = (CustomEditText)view.findViewById(R.id.cc_pincode);
        cc_city = (CustomEditText)view.findViewById(R.id.cc_city);
        cc_state = (CustomEditText)view.findViewById(R.id.cc_state);
        ib_get_gps_location = (ImageButton)view.findViewById(R.id.ib_get_gps_location);
        ib_get_gps_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentAddressFragment.this.showDialog();
                Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                if(location == null)
                    location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getActivity().getApplicationContext(), new GeocoderHandler());
                } else {
                    CurrentAddressFragment.this.hideDialog();
                    showSettingsAlert();
                }
             }
        });

        CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_owned_by);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.own_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addressForm = view.findViewById(R.id.getLocationFromFormLayout);
        gpsForm = view.findViewById(R.id.getLocationFromGPSLayout);

        registerChildView(addressForm, View.GONE);
        registerChildView(gpsForm, View.VISIBLE);

        registerFloatingActionButton(btnGps, addressForm);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == getCurrentView().findViewById(R.id.editCurrentAddressButton).getId())
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.getLocationFromFormLayout));
            }
            else
            {
                setVisibleChildView(getCurrentView().findViewById(R.id.getLocationFromGPSLayout));
            }
        }
    };

    private Location getLocation()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }

    }

    private Address getAddressFromCurrentLocation() throws IOException {
        Location location = getLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        List<Address> addressList = gcd.getFromLocation(latitude, longitude, 1);
        if(addressList != null && addressList.size()>0)
            return addressList.get(0);
        else
            return null;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                getActivity());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            CurrentAddressFragment.this.hideDialog();
            Bundle bundle  = message.getData();
            switch (message.what) {
                case 1:
                    cc_city.setText(bundle.getString(LocationAddress.CITY));
                    cc_state.setText(bundle.getString(LocationAddress.STATE));
                    cc_pincode.setText(bundle.getString(LocationAddress.PINCODE));
                    cc_street.setText(bundle.getString(LocationAddress.STREET) );
                    break;
                case 0:
                case -1:
                    String error = bundle.getString(LocationAddress.ERROR);
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG);
                    break;
            }
            setVisibleChildView(addressForm);
        }
    }
}
