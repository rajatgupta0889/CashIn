package com.mantralabsglobal.cashin.social;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.mantralabsglobal.cashin.service.FacebookService;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by pk on 7/4/2015.
 */
public class Facebook {

    public static void getFacebookProfile(AccessToken accessToken, final SocialBase.SocialListener<FacebookService.FacebookProfile> listener)
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

    }
}
