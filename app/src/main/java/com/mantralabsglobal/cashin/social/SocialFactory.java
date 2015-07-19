package com.mantralabsglobal.cashin.social;

/**
 * Created by hello on 7/18/2015.
 */
public class SocialFactory {

    public static final String FACEBOOK = "FACEBOOK";
    public static final String LINKEDIN = "LINKEDIN";

    private static final LinkedIn linkedIn = new LinkedIn();
    private static  final  Facebook facebook = new Facebook();

    public static <T extends SocialBase> T getSocialHelper(String socialName, Class<T> type)
    {
        if(FACEBOOK.equalsIgnoreCase(socialName))
        {
            return type.cast(facebook);
        }
        else if(LINKEDIN.equalsIgnoreCase(socialName))
        {
            return type.cast(linkedIn);
        }
        return null;
   }

    public static SocialBase getSocialHelper(String socialName)
    {
        if(FACEBOOK.equalsIgnoreCase(socialName))
        {
            return facebook;
        }
        else if(LINKEDIN.equalsIgnoreCase(socialName))
        {
            return linkedIn;
        }
        return null;
    }
}
