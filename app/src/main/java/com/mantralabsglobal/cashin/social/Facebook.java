package com.mantralabsglobal.cashin.social;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.FacebookService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pk on 7/4/2015.
 */
public class Facebook extends SocialBase<FacebookService.FacebookProfile>{

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me?fields=location,first_name,last_name,birthday,hometown,relationship_status,work";
    final static String CALLBACK = "http://www.mantralabsglobal.com/oauth_callback/";
    private static final Token EMPTY_TOKEN = null;

    @Override
    public OAuthService getOAuthService(Context context) {
        return new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(context.getResources().getString(R.string.facebook_app_id))
                .apiSecret(context.getResources().getString(R.string.facebook_secret))
                .callback(CALLBACK)
                .build();
    }

    @Override
    public Token getRequestToken(OAuthService service)
    {
        return EMPTY_TOKEN;
    }

    @Override
    public String getCallBackUrl() {
        return CALLBACK;
    }

    @Override
    public String getVerifierCode(String callbackUrl) {
        Uri uri = Uri.parse(callbackUrl);
        String verifier = uri.getQueryParameter("code");
        return verifier;
    }


    @Override
    protected String getProfileUrl() {
        return PROTECTED_RESOURCE_URL;
    }

    @Override
    protected FacebookService.FacebookProfile getProfileFromResponse(String responseBody) {
        Gson gson = new Gson();
        FacebookUserProfile facebookUserProfile = gson.fromJson(responseBody, FacebookUserProfile.class);

        return convertToFacebookFacebookProfile(facebookUserProfile);
    }

    private FacebookService.FacebookProfile convertToFacebookFacebookProfile(FacebookUserProfile facebookUserProfile) {
        FacebookService.FacebookProfile facebookProfile = null;
        if(facebookUserProfile != null)
        {
            facebookProfile = new FacebookService.FacebookProfile();
            facebookProfile.setCity(facebookUserProfile.getLocation().getName());
            facebookProfile.setConnectedAs(facebookUserProfile.getFirstName() + " " + facebookUserProfile.getLastName());
            facebookProfile.setDob(facebookUserProfile.getBirthday());
            facebookProfile.setHometown(facebookUserProfile.getHometown().getName());
            //Not available
            //facebookProfile.setRelationshipStatus(facebookUserProfile.getRelationshipStatus());
            if(facebookUserProfile.getWork() != null && facebookUserProfile.getWork().size()>0)
                facebookProfile.setWorkspace(facebookUserProfile.getWork().get(0).getEmployer().getName());
        }
        return facebookProfile;
    }

    /*public static void getFacebookProfile(AccessToken accessToken, final SocialBase.SocialListener<FacebookService.FacebookProfile> listener)
    {
        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                FacebookService.FacebookProfile facebookProfile = new FacebookService.FacebookProfile();
                facebookProfile.setDob(jsonObject.optString("birthday"));
                facebookProfile.setConnectedAs(jsonObject.optString("name"));

                JSONArray workArray = jsonObject.optJSONArray("work");
                if(workArray != null && workArray.length()>0)
                {
                    JSONObject workEx = workArray.optJSONObject(0);
                    if(workEx != null) {
                        JSONObject employer = workEx.optJSONObject("employer");
                        facebookProfile.setWorkspace(employer.optString("name"));
                    }
               }

                JSONObject jsonLocation =  jsonObject.optJSONObject("location");
                if(jsonLocation != null)
                {
                    facebookProfile.setCity(jsonLocation.optString("name"));
                }
                JSONObject jsonHomeTown =  jsonObject.optJSONObject("home_town");
                if(jsonHomeTown != null)
                {
                    facebookProfile.setHometown(jsonHomeTown.optString("name"));
                }
                facebookProfile.setRelationshipStatus(jsonObject.optString("relationship_status"));
                listener.onSuccess(facebookProfile);
            }
        }).executeAsync();

    }*/

    public class Employer {

        @Expose
        private String id;
        @Expose
        private String name;

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

    public class FacebookUserProfile {

        @Expose
        private String id;
        @Expose
        private String birthday;
        @Expose
        private String email;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @Expose
        private String gender;
        @Expose
        private Hometown hometown;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @Expose
        private String link;
        @Expose
        private Location location;
        @Expose
        private String locale;
        @Expose
        private String name;
        @Expose
        private Double timezone;
        @SerializedName("updated_time")
        @Expose
        private String updatedTime;
        @Expose
        private Boolean verified;
        @Expose
        private List<Work> work = new ArrayList<Work>();

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
         * The birthday
         */
        public String getBirthday() {
            return birthday;
        }

        /**
         *
         * @param birthday
         * The birthday
         */
        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        /**
         *
         * @return
         * The email
         */
        public String getEmail() {
            return email;
        }

        /**
         *
         * @param email
         * The email
         */
        public void setEmail(String email) {
            this.email = email;
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
         * The first_name
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         *
         * @return
         * The gender
         */
        public String getGender() {
            return gender;
        }

        /**
         *
         * @param gender
         * The gender
         */
        public void setGender(String gender) {
            this.gender = gender;
        }

        /**
         *
         * @return
         * The hometown
         */
        public Hometown getHometown() {
            return hometown;
        }

        /**
         *
         * @param hometown
         * The hometown
         */
        public void setHometown(Hometown hometown) {
            this.hometown = hometown;
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
         * The last_name
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         *
         * @return
         * The link
         */
        public String getLink() {
            return link;
        }

        /**
         *
         * @param link
         * The link
         */
        public void setLink(String link) {
            this.link = link;
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
         * The locale
         */
        public String getLocale() {
            return locale;
        }

        /**
         *
         * @param locale
         * The locale
         */
        public void setLocale(String locale) {
            this.locale = locale;
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
         * The timezone
         */
        public Double getTimezone() {
            return timezone;
        }

        /**
         *
         * @param timezone
         * The timezone
         */
        public void setTimezone(Double timezone) {
            this.timezone = timezone;
        }

        /**
         *
         * @return
         * The updatedTime
         */
        public String getUpdatedTime() {
            return updatedTime;
        }

        /**
         *
         * @param updatedTime
         * The updated_time
         */
        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        /**
         *
         * @return
         * The verified
         */
        public Boolean getVerified() {
            return verified;
        }

        /**
         *
         * @param verified
         * The verified
         */
        public void setVerified(Boolean verified) {
            this.verified = verified;
        }

        /**
         *
         * @return
         * The work
         */
        public List<Work> getWork() {
            return work;
        }

        /**
         *
         * @param work
         * The work
         */
        public void setWork(List<Work> work) {
            this.work = work;
        }

    }

    public class Hometown {

        @Expose
        private String id;
        @Expose
        private String name;

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

    public class Location {

        @Expose
        private String id;
        @Expose
        private String name;

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

    public class Location_ {

        @Expose
        private String id;
        @Expose
        private String name;

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

    public class Position {

        @Expose
        private String id;
        @Expose
        private String name;

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

    public class Work {

        @Expose
        private Employer employer;
        @Expose
        private Location_ location;
        @Expose
        private Position position;
        @SerializedName("start_date")
        @Expose
        private String startDate;
        @SerializedName("end_date")
        @Expose
        private String endDate;

        /**
         *
         * @return
         * The employer
         */
        public Employer getEmployer() {
            return employer;
        }

        /**
         *
         * @param employer
         * The employer
         */
        public void setEmployer(Employer employer) {
            this.employer = employer;
        }

        /**
         *
         * @return
         * The location
         */
        public Location_ getLocation() {
            return location;
        }

        /**
         *
         * @param location
         * The location
         */
        public void setLocation(Location_ location) {
            this.location = location;
        }

        /**
         *
         * @return
         * The position
         */
        public Position getPosition() {
            return position;
        }

        /**
         *
         * @param position
         * The position
         */
        public void setPosition(Position position) {
            this.position = position;
        }

        /**
         *
         * @return
         * The startDate
         */
        public String getStartDate() {
            return startDate;
        }

        /**
         *
         * @param startDate
         * The start_date
         */
        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        /**
         *
         * @return
         * The endDate
         */
        public String getEndDate() {
            return endDate;
        }

        /**
         *
         * @param endDate
         * The end_date
         */
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

    }
}
