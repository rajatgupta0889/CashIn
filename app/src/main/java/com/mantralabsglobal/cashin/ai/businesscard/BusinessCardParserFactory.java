package com.mantralabsglobal.cashin.ai.businesscard;


import com.mantralabsglobal.cashin.ai.businesscard.impl.BusinessCardParserImpl;

public class BusinessCardParserFactory {

	public IBusinessCardParser createBusinessCardParser() {
		return new BusinessCardParserImpl();
	}
	
}
