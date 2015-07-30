package com.mantralabsglobal.cashin.ai.businesscard;


import java.util.List;

public interface IBusinessCardParser {
	
	/**
	 * Process the input contact lines into a IContactInfo object
	 * @param document the raw input of non-classified contact information
	 * @return structured contact information extracted from the raw text input
	 */
	IContactInfo getContactInfo(List<String> document);
	
}
