package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mantralabsglobal.cashin.Application;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.rest.RestClient;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.mantralabsglobal.cashin.views.CustomEditText;

import org.brickred.socialauth.Career;
import org.brickred.socialauth.Education;
import org.brickred.socialauth.Position;
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
public class LinkedInFragment extends BaseFragment  {

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

    LinkedInService.LinkedInDetail linkedInDetail;
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


        registerChildView(vg_linkedInConnect, View.GONE);
        registerChildView(vg_linkedInProfile, View.GONE);

        showProgressDialog( getString(R.string.query_server),true,false);

        linkedInService = restClient.getLinkedInService();
        linkedInService.getLinkedInDetail(new Callback<LinkedInService.LinkedInDetail>() {
            @Override
            public void success(LinkedInService.LinkedInDetail linkedInDetail, Response response) {
                LinkedInFragment.this.linkedInDetail = linkedInDetail;
                if(linkedInDetail != null && (linkedInDetail.getEducation() != null || linkedInDetail.getWorkExperience() != null))
                    showLinkedInDetailForm(linkedInDetail);
                else
                    setVisibleChildView(vg_linkedInConnect);
                hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                setVisibleChildView(vg_linkedInConnect);
                hideProgressDialog();
            }
        });

    }

    protected void showLinkedInDetailForm(LinkedInService.LinkedInDetail linkedInDetail)
    {
        setVisibleChildView(vg_linkedInProfile);
        this.jobTitle.setText(linkedInDetail.getWorkExperience().getJobTitle());
        this.company.setText(linkedInDetail.getWorkExperience().getCompany());
        //TODO: Set all form fields

    }


    @OnClick(R.id.btn_linkedIn_connect)
    public void onConnectClick()
    {
        showProgressDialog(getString( R.string.waiting_for_linkedIn), true, false);
        socialAuthAdapter.authorize(getActivity(), SocialAuthAdapter.Provider.LINKEDIN);
    }

    private final class ResponseListener implements DialogListener
    {
        public void onComplete(Bundle values) {
            socialAuthAdapter.getUserProfileAsync(new ProfileDataListener());
            socialAuthAdapter.getCareerAsync(new SocialAuthListener<Career>() {
                @Override
                public void onExecute(String s, Career career) {
                    LinkedInService.LinkedInDetail linkedInDetail = new LinkedInService.LinkedInDetail();
                    linkedInDetail.setWorkExperience(new LinkedInService.WorkExperience());
                    linkedInDetail.setEducation(new LinkedInService.Education());
                    if(career.getPositions() != null && career.getPositions().length>0) {
                        Position position = career.getPositions()[0];
                        linkedInDetail.getWorkExperience().setCompany(position.getCompanyName());
                        linkedInDetail.getWorkExperience().setJobTitle(position.getTitle());
                        linkedInDetail.getWorkExperience().setTimePeriod(position.getStartDate().toString());
                    }
                    if(career.getEducations() != null && career.getEducations().length>0)
                    {
                        Education education = career.getEducations()[0];
                        linkedInDetail.getEducation().setCollege(education.getSchoolName());
                        linkedInDetail.getEducation().setDegree(education.getDegree());
                        linkedInDetail.getEducation().setFieldOfStudy(education.getFieldOfStudy());
                    }

                    showLinkedInDetailForm(linkedInDetail);
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
