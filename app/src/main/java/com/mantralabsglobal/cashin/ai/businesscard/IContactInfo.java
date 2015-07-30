package com.mantralabsglobal.cashin.ai.businesscard;

public interface IContactInfo {

	/**
	 * @return the full name of the individual (eg. John Smith, Edward E Wang, Susan Malick)
	 */
	String getName();
	
	/**
	 * @return the phone number formatted as XXX-XXX-XXXX
	 */
	String getPhoneNumber();
	
	/**
	 * @return the email address of the individual as it originally appears on the card
	 */
	String getEmailAddress();

	/**
	 *
	 * @return the name of the organization
	 */
	String getOrganization();
	
}
