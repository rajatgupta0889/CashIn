package com.mantralabsglobal.cashin.social;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.LinkedInService;
import com.mantralabsglobal.cashin.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by pk on 7/4/2015.
 */
public class LinkedIn {

    //private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,positions,location,email-address,school-name,degree,start-date,end-date)";
    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,positions,location,email-address)?format=json";
    private static final String TAG = LinkedIn.class.getSimpleName();
    final static String CALLBACK = "oauth://linkedin";

    //private static final String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~/connections:(id,last-name)";

    public static OAuthService getService(Context context, String callback)
    {

        return new ServiceBuilder()
                .provider(LinkedInApi.class)
                .apiKey(context.getResources().getString(R.string.linkedin_key))
                .apiSecret(context.getResources().getString(R.string.linkedin_secret))
                .callback(callback==null?CALLBACK:callback)
                .build();


    }

    public static LinkedInService.LinkedInDetail getLinkedInProfile2(Context context, String token, String secret )
    {
        OAuthService service = getService(context, null);
        Token accessToken = new Token(token, secret);

        OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();

        Gson gson = new Gson();
        LinkedInProfileResponse linkedInProfileResponse = gson.fromJson(response.getBody(), LinkedInProfileResponse.class);

        return convertToLinkedInDetail(linkedInProfileResponse);

    }

    private static LinkedInService.LinkedInDetail convertToLinkedInDetail(LinkedInProfileResponse linkedInProfileResponse)
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
            return linkedInDetail;

        }
        return null;
    }

    public static void getLinkedInProfile(Context context, Activity activity, final SocialBase.SocialListener<LinkedInService.LinkedInDetail> listener)
    {

        APIHelper apiHelper = APIHelper.getInstance(context);
        apiHelper.getRequest(activity, PROFILE_URL, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Log.i(TAG, apiResponse.getResponseDataAsJson().toString());
                JSONObject response = apiResponse.getResponseDataAsJson();
                Gson gson = new Gson();
                LinkedInProfileResponse linkedInProfileResponse = gson.fromJson(apiResponse.getResponseDataAsJson().toString(),LinkedInProfileResponse.class);
                if(linkedInProfileResponse != null)
                {
                    listener.onSuccess( convertToLinkedInDetail(linkedInProfileResponse));
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {

                listener.onFailure(LIApiError.getMessage());
            }
        });
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
