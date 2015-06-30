package com.mantralabsglobal.cashin.ui.fragment.tabs;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AddressService;

import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class PermanentAddressFragment extends AddressFragment {


    @Override
    protected void getAddressFromServer(Callback<AddressService.Address> serverCallback) {
        getAddressService().getPermanentAddress(serverCallback);
    }

    @Override
    public String getProgressDialogSaveText() {
        return getString(R.string.saving_permanent_address);
    }

    @Override
    public void createAddress(AddressService.Address address, Callback<AddressService.Address> callback) {
        getAddressService().createPermanentAddress(address,callback);
    }

    @Override
    public void updateAddress(AddressService.Address address, Callback<AddressService.Address> callback) {
        getAddressService().updatePermanentAddress(address, callback);
    }
}
