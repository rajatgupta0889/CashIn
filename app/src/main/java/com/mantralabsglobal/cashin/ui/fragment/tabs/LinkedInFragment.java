package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.social.LinkedIn;
import com.mantralabsglobal.cashin.social.SocialBase;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class LinkedInFragment extends BaseBindableFragment<LinkedInService.LinkedInDetail> {

    private static final String TAG = "LinkedInFragment";
    @InjectView(R.id.btn_linkedIn_connect)
    ImageButton btn_linkedIn;

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
        if( LISessionManager.getInstance(getActivity()).getSession() != null &&
                !LISessionManager.getInstance(getActivity()).getSession().getAccessToken().isExpired())
        {
            LinkedIn.getLinkedInProfile(getActivity().getApplicationContext(), getActivity(), listener);
        }
        else {
            setVisibleChildView(vg_linkedInConnect);
        }

    }

    @OnClick(R.id.btn_linkedIn_connect)
    public void onConnectClick()
    {
        showProgressDialog(getString(R.string.waiting_for_linkedIn), true, false);

        if( LISessionManager.getInstance(getActivity()).getSession() != null &&
        !LISessionManager.getInstance(getActivity()).getSession().getAccessToken().isExpired())
        {
            LinkedIn.getLinkedInProfile(getActivity().getApplicationContext(), getActivity(), listener);
        }
        else {
            LISessionManager.getInstance(getActivity().getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                            LinkedIn.getLinkedInProfile(getActivity().getApplicationContext(), getActivity(), listener);
                        }

                        @Override
                        public void onAuthError(LIAuthError error) {
                            hideProgressDialog();
                            Log.v(TAG, error.toString());
                            setVisibleChildView(vg_linkedInConnect);
                        }
                    },
                    true);
        }
    }

    private SocialBase.SocialListener<LinkedInService.LinkedInDetail> listener = new SocialBase.SocialListener<LinkedInService.LinkedInDetail>() {
        @Override
        public void onSuccess(LinkedInService.LinkedInDetail linkedInDetail) {
            hideProgressDialog();
            bindDataToForm(linkedInDetail);
        }

        @Override
        public void onFailure(String message) {
            hideProgressDialog();
            showToastOnUIThread(message);
            setVisibleChildView(vg_linkedInConnect);
        }
    };

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    @Override
    public void bindDataToForm(LinkedInService.LinkedInDetail value) {
        setVisibleChildView(vg_linkedInProfile);
        if(value != null) {
            if(value.getWorkExperience() != null) {
                this.jobTitle.setText(value.getWorkExperience().getJobTitle());
                this.company.setText(value.getWorkExperience().getCompany());
                this.period.setText(value.getWorkExperience().getTimePeriod());
            }
        }
        //TODO: Set all form fields
    }

    @Override
    public LinkedInService.LinkedInDetail getDataFromForm(LinkedInService.LinkedInDetail linkedInDetail) {
        if(linkedInDetail == null)
            linkedInDetail = new LinkedInService.LinkedInDetail();

        if(linkedInDetail.getWorkExperience() == null)
            linkedInDetail.setWorkExperience(new LinkedInService.WorkExperience());

        if(linkedInDetail.getEducation() == null)
            linkedInDetail.setEducation(new LinkedInService.Education());

        linkedInDetail.getWorkExperience().setCompany(company.getText().toString());
        linkedInDetail.getWorkExperience().setTimePeriod(period.getText().toString());
        linkedInDetail.getWorkExperience().setJobTitle(jobTitle.getText().toString());

        return linkedInDetail;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getActivity().getApplicationContext()).onActivityResult(getActivity(), requestCode, resultCode, data);
    }

   /* private final class ResponseListener implements DialogListener
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
*/
       /* @Override
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
    }*/


}
