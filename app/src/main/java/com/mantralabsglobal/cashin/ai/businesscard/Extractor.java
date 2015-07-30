package com.mantralabsglobal.cashin.ai.businesscard;

import java.util.List;

import com.mantralabsglobal.cashin.ai.businesscard.impl.ContactInfo;

public interface Extractor {

	/**
	 * Attempts to extract some knowledge of the Contact from the input lines
	 * of text that have not yet been classified. This method modifies the input
	 * ContactInfo parameter by setting any information that the specific extractor
	 * identifies, then returns the remaining unknown lines for further processing.
	 * 
	 * @param lines The primary input of liens of contact information that have not been identified.
	 * @param contact Information that has already been extracted. Any implementations will modify this class directly to output extracted information.
	 * @return List of the remaining lines of contact information that have not yet been identified.
	 */
	List<String> extract(List<String> lines, ContactInfo contact);
	
}
