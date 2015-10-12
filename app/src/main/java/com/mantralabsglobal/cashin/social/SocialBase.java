package com.mantralabsglobal.cashin.social;

import android.content.Context;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Created by pk on 7/4/2015.
 */
public abstract class SocialBase<T> {

    public abstract OAuthService getOAuthService(Context context);

    protected abstract String getProfileUrl();

    public T getSocialProfile(Context context, Token accessToken)
    {
        OAuthRequest request = new OAuthRequest(Verb.GET, getProfileUrl());
        getOAuthService(context).signRequest(accessToken, request);
        Response response = request.send();

        return getProfileFromResponse(response.getBody());
    }


    protected abstract T getProfileFromResponse(String responseBody);

    public Token getAccessToken(String token, String secret){
        return new Token(token, secret);
    }

    public Token getRequestToken(OAuthService service)
    {
        return service.getRequestToken();
    }

    public abstract String getCallBackUrl();

    public abstract String getVerifierCode(String url1);

    public static interface SocialListener<T>
    {
        void onSuccess(T t);
        void onFailure(String message);
    }

}
