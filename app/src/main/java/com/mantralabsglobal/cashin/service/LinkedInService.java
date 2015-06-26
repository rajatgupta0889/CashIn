package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface LinkedInService {

    @GET("/user/linkedIn")
    void getLinkedInDetail(Callback<LinkedInDetail> callback);

    @POST("/user/linkedIn")
    void setLinkedInDetail(LinkedInDetail linkedInDetail, Callback<LinkedInDetail> callback);

    public static class LinkedInDetail{

        @SerializedName("workexperiance")
        private WorkExperience workExperience;

        @SerializedName("education")
        private Education education;

        public WorkExperience getWorkExperience() {
            return workExperience;
        }

        public void setWorkExperience(WorkExperience workExperience) {
            this.workExperience = workExperience;
        }

        public Education getEducation() {
            return education;
        }

        public void setEducation(Education education) {
            this.education = education;
        }
    }

    public static class WorkExperience
    {
        private String jobTitle;
        private String company;
        private String timePeriod;

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getTimePeriod() {
            return timePeriod;
        }

        public void setTimePeriod(String timePeriod) {
            this.timePeriod = timePeriod;
        }
    }

    public static class Education{

        private String college;
        private String degree;
        private String fieldOfStudy;

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getFieldOfStudy() {
            return fieldOfStudy;
        }

        public void setFieldOfStudy(String fieldOfStudy) {
            this.fieldOfStudy = fieldOfStudy;
        }
    }


}
