package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;

import org.brickred.socialauth.Career;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class LinkedInFragment extends BaseBindableFragment<LinkedInService.LinkedInDetail> {

    @InjectView(R.id.btn_linkedIn_connect)
    Button btn_linkedIn;

    @InjectView(R.id.ll_linkedIn_connect)
    ViewGroup vg_linkedInConnect;

    @InjectView(R.id.rl_linkedin_details)
    ViewGroup vg_linkedInProfile;

    @InjectView(R.id.et_name)
    EditText et_name;

    @InjectView(R.id.cs_job_title)
    CustomEditText jobTitle;
    @InjectView(R.id.cs_company)
    CustomEditText company;
    @InjectView(R.id.cs_time_period)
    CustomEditText period;

    LinkedInService linkedInService;

    SocialAuthAdapter socialAuthAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_linkedin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socialAuthAdapter = new SocialAuthAdapter(new ResponseListener());

        RestClient restClient =((Application) getActivity().getApplication()).getRestClient();


        registerChildView(vg_linkedInConnect, View.VISIBLE);
        registerChildView(vg_linkedInProfile, View.GONE);

        linkedInService = restClient.getLinkedInService();

        reset(false);

    }

    @Override
    protected void onUpdate(LinkedInService.LinkedInDetail updatedData, Callback<LinkedInService.LinkedInDetail> saveCallback) {
        linkedInService.updateLinkedInDetail(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(LinkedInService.LinkedInDetail updatedData, Callback<LinkedInService.LinkedInDetail> saveCallback) {
        linkedInService.createLinkedInDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<LinkedInService.LinkedInDetail> dataCallback) {
        linkedInService.getLinkedInDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vg_linkedInConnect);
    }

    @OnClick(R.id.btn_linkedIn_connect)
    public void onConnectClick()
    {
        showProgressDialog(getString( R.string.waiting_for_linkedIn), true, false);
        socialAuthAdapter.authorize(getActivity(), SocialAuthAdapter.Provider.LINKEDIN);
    }

    @Override
    public void bindDataToForm(LinkedInService.LinkedInDetail value) {
        setVisibleChildView(vg_linkedInProfile);
        if(value != null) {
            if(value.getWorkExperience() != null) {
                this.jobTitle.setText(value.getWorkExperience().getJobTitle());
                this.company.setText(value.getWorkExperience().getCompany());
            }
        }
        //TODO: Set all form fields
    }

    @Override
    public LinkedInService.LinkedInDetail getDataFromForm(LinkedInService.LinkedInDetail linkedInDetail) {
        return linkedInDetail;
    }


    private final class ResponseListener implements DialogListener
    {
        public void onComplete(Bundle values) {
            socialAuthAdapter.getUserProfileAsync(new ProfileDataListener());
            socialAuthAdapter.getCareerAsync(new SocialAuthListener<Career>() {
                @Override
                public void onExecute(String s, Career career) {

                    LinkedInService.LinkedInDetail linkedInDetail = LinkedInService.LinkedInAdapter.fromCareer(career);
                    bindDataToForm(linkedInDetail);
                    hideProgressDialog();

                }

                @Override
                public void onError(SocialAuthError socialAuthError) {
                    Log.e("LinkedInFragment", "failed to get career", socialAuthError);
                    hideProgressDialog();
                    showToastOnUIThread(socialAuthError.getMessage());
                }
            });

        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.e("LinkedInFragment", socialAuthError.getMessage(), socialAuthError);
            hideProgressDialog();
            showToastOnUIThread(socialAuthError.getMessage());
        }

        @Override
        public void onCancel() {
            Log.w("LinkedInFragment", "Linkedin connect cancelled");
            hideProgressDialog();
        }

        @Override
        public void onBack() {
            Log.w("LinkedInFragment", "Linkedin connect cancelled by back");
            hideProgressDialog();
        }
    }

    // To receive the profile response after authentication
    private final class ProfileDataListener implements SocialAuthListener<Profile> {

        @Override
        public void onExecute(String s, Profile profileMap) {
            Log.d("LinkedInFragment", "Receiving Data");
            Log.d("LinkedInFragment", "Validate ID         = " + profileMap.getValidatedId());
            Log.d("LinkedInFragment", "First Name          = " + profileMap.getFirstName());
            Log.d("LinkedInFragment", "Last Name           = " + profileMap.getLastName());
            Log.d("LinkedInFragment", "Email               = " + profileMap.getEmail());
            Log.d("LinkedInFragment", "Country                  = " + profileMap.getCountry());
            Log.d("LinkedInFragment", "Language                 = " + profileMap.getLanguage());
            Log.d("LinkedInFragment", "Location                 = " + profileMap.getLocation());
            Log.d("LinkedInFragment", "Profile Image URL  = " + profileMap.getProfileImageURL());


        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            showToastOnUIThread(socialAuthError.getMessage());
        }
    }


}
