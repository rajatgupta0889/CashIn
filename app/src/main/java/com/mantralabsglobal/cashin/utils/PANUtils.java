package com.mantralabsglobal.cashin.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by hello on 7/26/2015.
 */
public class PANUtils {

    public static final String NAME_FIELD = "NAME";
    public static final String PAN_NUMBER_FIELD = "PAN_NUMBER_FIELD";

    private static final String GOVT_OF_INDIA = "GOVT. OF INDIA";
    private static final String INCOME_TAX_DEPARTMENT = "INCOME TAX DEPARTMENT";
    private static final String PERMANENT_ACCOUNT_NUMBER = "Permanent Account Number";

    public static String getName(String [] contentArr, int minLength)
    {
        String name = getNextNonEmpty(INCOME_TAX_DEPARTMENT + " " + GOVT_OF_INDIA, contentArr, minLength);
        if(StringUtils.isEmpty(name))
        {
            name = getNextNonEmpty(INCOME_TAX_DEPARTMENT, contentArr, minLength);
        }
        if(StringUtils.isEmpty(name))
        {
            name = getNextNonEmpty(GOVT_OF_INDIA, contentArr, minLength);
        }
        if(StringUtils.isEmpty(name)) {
            //TRY Backwards
            int matchIndex = getMatchIndex(PERMANENT_ACCOUNT_NUMBER, contentArr, 12);
            if(matchIndex>=0)
            {
                int skipped = 0;
                for(int i = matchIndex-1; i>=0 && skipped<=1 ;i--)
                {
                    if( StringUtils.isNoneEmpty(contentArr[i])&& contentArr[i].length()>=3)
                    {
                        if(skipped == 2) {
                            name = contentArr[i];
                            break;
                        }
                        else
                        {
                            skipped++;
                        }
                    }
                }
            }
        }
        return name;
    }

    public static String getPANNumber(String [] contentArr, int minLength )
    {
        return getNextNonEmpty(PERMANENT_ACCOUNT_NUMBER, contentArr, minLength);
    }

    public static String getNextNonEmpty(String beforeString, String [] contentArr, int minLength)
    {
        String value = null;
        //Identify NAME
        int matchingIndex = getMatchIndex(beforeString,contentArr,beforeString.length()/2);

        if(matchingIndex>=0) {
            //Get first not empty staring from this index
            for (int i = matchingIndex+1; i < contentArr.length; i++)
            {
                if( StringUtils.isNoneEmpty(contentArr[i])&& contentArr[i].length()>=minLength)
                {
                    value = contentArr[i];
                    break;
                }
            }
        }
        return value;
    }

    public static int getMatchIndex(String content, String [] contentArr, int threshold)
    {
        //Identify NAME
        int matchingIndex = -1;
        for(int i=0;i<contentArr.length;i++)
        {
            int distance = StringUtils.getLevenshteinDistance(content, contentArr[i], threshold);
            if(distance >=0) {
                matchingIndex = i;
                break;
            }
        }
        return matchingIndex;
    }

}
