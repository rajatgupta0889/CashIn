package com.mantralabsglobal.cashin.social;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.mantralabsglobal.cashin.utils.DateUtils;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pk on 7/4/2015.
 */
public class LinkedIn extends SocialBase<LinkedInService.LinkedInDetail>{

    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,positions,location,email-address,educations)?format=json";
    //private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,positions,location,email-address)?format=json";
    private static final String TAG = LinkedIn.class.getSimpleName();
    final static String CALLBACK = "oauth://linkedin";

    //private static final String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~/connections:(id,last-name)";

    public OAuthService getOAuthService(Context context)
    {

        return new ServiceBuilder()
                //.provider(LinkedInApi.withScopes("r_fullprofile"))
                .provider(LinkedInApi.class)
                .apiKey(context.getResources().getString(R.string.linkedin_key))
                .apiSecret(context.getResources().getString(R.string.linkedin_secret))
                .callback(CALLBACK)
                .build();
    }

    @Override
    protected String getProfileUrl() {
        return PROFILE_URL;
    }

    @Override
    protected LinkedInService.LinkedInDetail getProfileFromResponse(String responseBody) {
        Gson gson = new Gson();
        LinkedInProfileResponse linkedInProfileResponse = gson.fromJson(responseBody, LinkedInProfileResponse.class);

        return convertToLinkedInDetail(linkedInProfileResponse);
    }

    private LinkedInService.LinkedInDetail convertToLinkedInDetail(LinkedInProfileResponse linkedInProfileResponse)
    {
        if(linkedInProfileResponse != null)
        {
            LinkedInService.LinkedInDetail linkedInDetail = new LinkedInService.LinkedInDetail();
            linkedInDetail.setConnectedAs(linkedInProfileResponse.getFirstName() + " " + linkedInProfileResponse.getLastName());
            linkedInDetail.setWorkExperience(new LinkedInService.WorkExperience());
            linkedInDetail.setEducation(new LinkedInService.Education());
            if(linkedInProfileResponse.getPositions() != null && linkedInProfileResponse.getPositions().getValues().size()>0)
            {
                linkedInDetail.getWorkExperience().setCompany(linkedInProfileResponse.getPositions().getValues().get(0).getCompany().getName());
                linkedInDetail.getWorkExperience().setJobTitle(linkedInProfileResponse.getPositions().getValues().get(0).getTitle());
                StartDate sd = linkedInProfileResponse.getPositions().getValues().get(0).getStartDate();
                if(sd != null)
                {
                    linkedInDetail.getWorkExperience().setTimePeriod( DateUtils.getYearsPassed(sd.getYear(), sd.getMonth(),1) + " Years ");
                }
            }
            if(linkedInProfileResponse.getEducations() != null && linkedInProfileResponse.getEducations().getValues().size()>0)
            {
                linkedInDetail.getEducation().setCollege(linkedInProfileResponse.getEducations().getValues().get(0).getSchoolName());
                linkedInDetail.getEducation().setDegree(linkedInProfileResponse.getEducations().getValues().get(0).getDegree());
                linkedInDetail.getEducation().setFieldOfStudy(linkedInProfileResponse.getEducations().getValues().get(0).getFieldOfStudy());
            }
            return linkedInDetail;

        }
        return null;
    }


    @Override
    public String getCallBackUrl() {
        return CALLBACK;
    }

    @Override
    public String getVerifierCode(String callbackUrl) {
        Uri uri = Uri.parse(callbackUrl);
        String verifier = uri.getQueryParameter("oauth_verifier");
        return verifier;
    }

    public class Company {

        @Expose
        private Integer id;
        @Expose
        private String industry;
        @Expose
        private String name;
        @Expose
        private String size;
        @Expose
        private String ticker;
        @Expose
        private String type;

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The industry
         */
        public String getIndustry() {
            return industry;
        }

        /**
         *
         * @param industry
         * The industry
         */
        public void setIndustry(String industry) {
            this.industry = industry;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The size
         */
        public String getSize() {
            return size;
        }

        /**
         *
         * @param size
         * The size
         */
        public void setSize(String size) {
            this.size = size;
        }

        /**
         *
         * @return
         * The ticker
         */
        public String getTicker() {
            return ticker;
        }

        /**
         *
         * @param ticker
         * The ticker
         */
        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        /**
         *
         * @return
         * The type
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @param type
         * The type
         */
        public void setType(String type) {
            this.type = type;
        }

    }

    public class Country {

        @Expose
        private String code;

        /**
         *
         * @return
         * The code
         */
        public String getCode() {
            return code;
        }

        /**
         *
         * @param code
         * The code
         */
        public void setCode(String code) {
            this.code = code;
        }

    }

    public class LinkedInProfileResponse {

        @Expose
        private String emailAddress;
        @Expose
        private String firstName;
        @Expose
        private String id;
        @Expose
        private String lastName;
        @Expose
        private Location location;
        @Expose
        private Positions positions;

        @Expose
        private Educations educations;


        /**
         *
         * @return
         * The educations
         */
        public Educations getEducations() {
            return educations;
        }

        /**
         *
         * @param educations
         * The educations
         */
        public void setEducations(Educations educations) {
            this.educations = educations;
        }
        /**
         *
         * @return
         * The emailAddress
         */
        public String getEmailAddress() {
            return emailAddress;
        }

        /**
         *
         * @param emailAddress
         * The emailAddress
         */
        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        /**
         *
         * @return
         * The firstName
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         *
         * @param firstName
         * The firstName
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The lastName
         */
        public String getLastName() {
            return lastName;
        }

        /**
         *
         * @param lastName
         * The lastName
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         *
         * @return
         * The location
         */
        public Location getLocation() {
            return location;
        }

        /**
         *
         * @param location
         * The location
         */
        public void setLocation(Location location) {
            this.location = location;
        }

        /**
         *
         * @return
         * The positions
         */
        public Positions getPositions() {
            return positions;
        }

        /**
         *
         * @param positions
         * The positions
         */
        public void setPositions(Positions positions) {
            this.positions = positions;
        }

    }

    public class Educations {

        @SerializedName("_total")
        @Expose
        private Integer Total;
        @Expose
        private List<Education> values = new ArrayList<Education>();

        /**
         *
         * @return
         * The Total
         */
        public Integer getTotal() {
            return Total;
        }

        /**
         *
         * @param Total
         * The _total
         */
        public void setTotal(Integer Total) {
            this.Total = Total;
        }

        /**
         *
         * @return
         * The values
         */
        public List<Education> getValues() {
            return values;
        }

        /**
         *
         * @param values
         * The values
         */
        public void setValues(List<Education> values) {
            this.values = values;
        }

    }

    public class EndDate {

        @Expose
        private Integer year;

        /**
         *
         * @return
         * The year
         */
        public Integer getYear() {
            return year;
        }

        /**
         *
         * @param year
         * The year
         */
        public void setYear(Integer year) {
            this.year = year;
        }

    }

    public class Education {

        @Expose
        private String degree;
        @Expose
        private EndDate endDate;
        @Expose
        private String fieldOfStudy;
        @Expose
        private Integer id;
        @Expose
        private String schoolName;
        @Expose
        private StartDate startDate;

        /**
         *
         * @return
         * The degree
         */
        public String getDegree() {
            return degree;
        }

        /**
         *
         * @param degree
         * The degree
         */
        public void setDegree(String degree) {
            this.degree = degree;
        }

        /**
         *
         * @return
         * The endDate
         */
        public EndDate getEndDate() {
            return endDate;
        }

        /**
         *
         * @param endDate
         * The endDate
         */
        public void setEndDate(EndDate endDate) {
            this.endDate = endDate;
        }

        /**
         *
         * @return
         * The fieldOfStudy
         */
        public String getFieldOfStudy() {
            return fieldOfStudy;
        }

        /**
         *
         * @param fieldOfStudy
         * The fieldOfStudy
         */
        public void setFieldOfStudy(String fieldOfStudy) {
            this.fieldOfStudy = fieldOfStudy;
        }

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The schoolName
         */
        public String getSchoolName() {
            return schoolName;
        }

        /**
         *
         * @param schoolName
         * The schoolName
         */
        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        /**
         *
         * @return
         * The startDate
         */
        public StartDate getStartDate() {
            return startDate;
        }

        /**
         *
         * @param startDate
         * The startDate
         */
        public void setStartDate(StartDate startDate) {
            this.startDate = startDate;
        }

    }

    public class Location {

        @Expose
        private Country country;
        @Expose
        private String name;

        /**
         *
         * @return
         * The country
         */
        public Country getCountry() {
            return country;
        }

        /**
         *
         * @param country
         * The country
         */
        public void setCountry(Country country) {
            this.country = country;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public class Positions {

        @SerializedName("_total")
        @Expose
        private Integer Total;
        @Expose
        private List<Value> values = new ArrayList<Value>();

        /**
         *
         * @return
         * The Total
         */
        public Integer getTotal() {
            return Total;
        }

        /**
         *
         * @param Total
         * The _total
         */
        public void setTotal(Integer Total) {
            this.Total = Total;
        }

        /**
         *
         * @return
         * The values
         */
        public List<Value> getValues() {
            return values;
        }

        /**
         *
         * @param values
         * The values
         */
        public void setValues(List<Value> values) {
            this.values = values;
        }

    }


    public class StartDate {

        @Expose
        private Integer month;
        @Expose
        private Integer year;

        /**
         *
         * @return
         * The month
         */
        public Integer getMonth() {
            return month;
        }

        /**
         *
         * @param month
         * The month
         */
        public void setMonth(Integer month) {
            this.month = month;
        }

        /**
         *
         * @return
         * The year
         */
        public Integer getYear() {
            return year;
        }

        /**
         *
         * @param year
         * The year
         */
        public void setYear(Integer year) {
            this.year = year;
        }

    }

    public class Value {

        @Expose
        private Company company;
        @Expose
        private Integer id;
        @Expose
        private Boolean isCurrent;
        @Expose
        private StartDate startDate;
        @Expose
        private String title;

        /**
         *
         * @return
         * The company
         */
        public Company getCompany() {
            return company;
        }

        /**
         *
         * @param company
         * The company
         */
        public void setCompany(Company company) {
            this.company = company;
        }

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The isCurrent
         */
        public Boolean getIsCurrent() {
            return isCurrent;
        }

        /**
         *
         * @param isCurrent
         * The isCurrent
         */
        public void setIsCurrent(Boolean isCurrent) {
            this.isCurrent = isCurrent;
        }

        /**
         *
         * @return
         * The startDate
         */
        public StartDate getStartDate() {
            return startDate;
        }

        /**
         *
         * @param startDate
         * The startDate
         */
        public void setStartDate(StartDate startDate) {
            this.startDate = startDate;
        }

        /**
         *
         * @return
         * The title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @param title
         * The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

    }



}
