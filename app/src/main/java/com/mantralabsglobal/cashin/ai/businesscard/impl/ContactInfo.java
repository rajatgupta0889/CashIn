package com.mantralabsglobal.cashin.ai.businesscard.impl;

import com.mantralabsglobal.cashin.ai.businesscard.IContactInfo;

import java.util.ArrayList;
import java.util.Collection;

public class ContactInfo implements IContactInfo {

	private String name;
	private String emailAddress;
	private String organization;
	private String phoneNumber;
	private String faxNumber;
	private Collection<String> other;
	
	public String getName() {
		return name;
	}
	public ContactInfo setName(String name) {
		this.name = name;
		return this;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public ContactInfo setEmailAddress(String email) {
		this.emailAddress = email;
		return this;
	}
	public String getOrganization() {
		return organization;
	}
	public ContactInfo setOrganization(String organization) {
		this.organization = organization;
		return this;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public ContactInfo setPhoneNumber(String phone) {
		this.phoneNumber = phone;
		return this;
	}
	public String getFaxNumber() {
		return faxNumber;
	}
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	public Collection<String> getOther() {
		initOther();
		return other;
	}
	public ContactInfo setOther(Collection<String> other) {
		this.other = other;
		return this;
	}
	public ContactInfo addOther(String other) {
		initOther();
		this.other.add(other);
		return this;
	}
	
	/**
	 * Initialize the 'other' collection
	 * as a simple array list
	 */
	private void initOther() {
		if(this.other == null) {
			this.other = new ArrayList<String>();
		}
	}
}
