package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface LinkedInService {

    @GET("/user/linkedIn")
    void getLinkedInDetail(Callback<LinkedInDetail> callback);

    @POST("/user/linkedIn")
    void createLinkedInDetail(@Body LinkedInDetail linkedInDetail, Callback<LinkedInDetail> callback);

    @PUT("/user/linkedIn")
    void updateLinkedInDetail(@Body LinkedInDetail linkedInDetail, Callback<LinkedInDetail> callback);

   /* public static class LinkedInAdapter
    {
        public static LinkedInDetail fromCareer(Career career)
        {
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
                org.brickred.socialauth.Education education = career.getEducations()[0];
                linkedInDetail.getEducation().setCollege(education.getSchoolName());
                linkedInDetail.getEducation().setDegree(education.getDegree());
                linkedInDetail.getEducation().setFieldOfStudy(education.getFieldOfStudy());
            }
            return linkedInDetail;
        }
    }
*/
    public static class LinkedInDetail{

        @SerializedName("workexperiance")
        private WorkExperience workExperience;

        @SerializedName("education")
        private Education education;

       private String connectedAs;

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

       public void setConnectedAs(String connectedAs) {
           this.connectedAs = connectedAs;
       }

       public String getConnectedAs() {
           return connectedAs;
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
