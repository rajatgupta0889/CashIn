package com.mantralabsglobal.cashin.utils;

import android.text.TextUtils;

import com.mantralabsglobal.cashin.ai.businesscard.BusinessCardParserFactory;
import com.mantralabsglobal.cashin.ai.businesscard.IContactInfo;
import com.mantralabsglobal.cashin.ai.businesscard.impl.ContactInfo;
import com.mantralabsglobal.cashin.service.BusinessCardService;

import java.util.List;

/**
 * Created by hello on 7/30/2015.
 */
public class BusinessCardUtils {

    public static BusinessCardService.BusinessCardDetail enrich(List<String> contentArray, BusinessCardService.BusinessCardDetail businessCardDetail)
    {
        BusinessCardParserFactory factory = new BusinessCardParserFactory();
        IContactInfo contactInfo = factory.createBusinessCardParser().getContactInfo(contentArray);

        if(businessCardDetail == null)
            businessCardDetail = new BusinessCardService.BusinessCardDetail();
        if(contactInfo != null )
        {
            if(!TextUtils.isEmpty(contactInfo.getName()))
                businessCardDetail.setName(contactInfo.getName());
            if(!TextUtils.isEmpty(contactInfo.getEmailAddress()))
                businessCardDetail.setEmail(contactInfo.getEmailAddress());
            businessCardDetail.setEmployerName(contactInfo.getOrganization());
        }
        return businessCardDetail;
    }
}
