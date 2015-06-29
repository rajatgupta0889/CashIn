package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.utils.LocationAddress;
import com.mantralabsglobal.cashin.service.AddressService;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class CurrentAddressFragment extends BaseFragment implements Bindable<AddressService.Address> {

    @NotEmpty
    @InjectView(R.id.cc_street)
    CustomEditText cc_street;

    @NotEmpty
    @Digits()
    @InjectView(R.id.cc_pincode)
    CustomEditText cc_pincode;

    @NotEmpty
    @InjectView(R.id.cc_city)
    CustomEditText cc_city;

    @NotEmpty
    @InjectView(R.id.cc_state)
    CustomEditText cc_state;

    @InjectView(R.id.cs_owned_by)
    CustomSpinner cs_own;

    @InjectView(R.id.btn_edit_address)
    Button btn_editAddress;

    @InjectView(R.id.ib_get_gps_location)
    ImageButton ib_get_gps_location;

    @InjectView(R.id.rb_rent)
    RadioButton rb_rented;

    @InjectView(R.id.rb_own)
    RadioButton rb_own;

    @InjectView(R.id.fab_get_loc_from_gps)
    FloatingActionButton btnGetLocationFromGPS;

    @InjectView(R.id.vg_current_address_form)
    ViewGroup vg_addressForm;

    @InjectView(R.id.vg_gps_launcher)
    ViewGroup vg_gpsLauncher;

    private AddressService mAddressService;

    private AddressService.Address addressOnServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);

        CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_owned_by);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.own_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        registerChildView(vg_addressForm, View.GONE);
        registerChildView(vg_gpsLauncher, View.VISIBLE);
        registerFloatingActionButton(btnGetLocationFromGPS, vg_addressForm);

        if(addressOnServer == null) {
            showProgressDialog(getString(R.string.waiting_for_server));
            getAddressService().getAddress(new Callback<List<AddressService.Address>>() {
                @Override
                public void success(List<AddressService.Address> addressList, Response response) {
                    hideProgressDialog();
                    for (AddressService.Address address : addressList) {
                        if ("current".equalsIgnoreCase(address.getType())) {
                            addressOnServer = address;
                        }
                    }

                    if (addressOnServer != null) {
                        bindDataToForm(addressOnServer);
                        showAddressForm();
                    } else {
                        showToastOnUIThread(getString(R.string.not_present_on_server));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgressDialog();
                    if (error != null)
                        showToastOnUIThread(error.getMessage());
                    hideProgressDialog();
                }
            });
        }
    }


    @OnClick(R.id.btn_edit_address)
    public void showAddressForm()
    {
        setVisibleChildView(vg_addressForm);
    }

    @OnClick(R.id.fab_get_loc_from_gps)
    public void showGPSLauncher()
    {
        setVisibleChildView(vg_gpsLauncher);
    }

    @OnClick(R.id.ib_get_gps_location)
    public void getLocationFromGPS() {
        showProgressDialog(getString(R.string.waiting_for_gps));

        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getCurrentAddress(getActivity(), new LocationAddress.AddressListener() {
            @Override
            public void onAddressAquired(final AddressService.Address address) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CurrentAddressFragment.this.onGPSAddressAquired(address);
                        hideProgressDialog();
                    }
                });

            }

            @Override
            public void onError(final Throwable error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToastOnUIThread(getString(R.string.gps_failed) + error.getMessage());
                        hideProgressDialog();
                    }
                });

            }
        });

       }

    public void onGPSAddressAquired(AddressService.Address address)
    {
        if(address != null) {
            bindDataToForm(address);
            showAddressForm();
        }
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

    @OnClick(R.id.btn_save)
    public void onSave()
    {
        if(canSave())
        {
            showProgressDialog( getString(R.string.saving_current_address));
            AddressService.Address address = addressOnServer;
            if(address == null)
               address = new AddressService.Address();
            getAddressService().setAddress(getDataFromForm(address), new Callback<AddressService.Address>() {
                @Override
                public void success(AddressService.Address address, Response response) {
                    addressOnServer = address;
                    showToastOnUIThread(getString(R.string.save_sucess));
                    hideProgressDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgressDialog();
                    showToastOnUIThread(error.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel()
    {

    }

    public AddressService getAddressService() {
        if(mAddressService == null) {
            mAddressService = ((Application) getActivity().getApplication()).getRestClient().getAddressService();
        }
        return mAddressService;
    }

    public void setAddressService(AddressService addressService) {
        this.mAddressService = addressService;
    }

    @Override
    public void bindDataToForm(final AddressService.Address address) {
        if(address != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cc_city.setText(address.getCity());
                    cc_state.setText(address.getState());
                    cc_pincode.setText(address.getPincode());
                    cc_street.setText(address.getStreet());
                    rb_rented.setChecked(address.isHouseRented());
                    rb_own.setChecked(!address.isHouseRented());
                    cs_own.getSpinner().setSelection(((ArrayAdapter<String>) cs_own.getAdapter()).getPosition(address.getOwn()));
                }
            });

        }
    }

    @Override
    public AddressService.Address getDataFromForm(AddressService.Address address) {
        address.setStreet(cc_street.getText().toString());
        address.setPincode(cc_pincode.getText().toString());
        address.setCity(cc_city.getText().toString());
        address.setState(cc_state.getText().toString());
        address.setIsHouseRented(rb_rented.isChecked());
        address.setOwn(cs_own.getSpinner().getSelectedItem().toString());
        return address;
    }

}
