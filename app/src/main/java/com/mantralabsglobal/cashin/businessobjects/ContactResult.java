package com.mantralabsglobal.cashin.businessobjects;

import java.io.Serializable;
import java.util.List;


public class ContactResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String contactId;
	private String contactName;
	private List<ResultItem> results;
	
	public ContactResult(String id, String name, List<ResultItem> results) {
		this.contactId = id;
        this.contactName = name;
		this.results = results;
	}
	
	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public List<ResultItem> getResults() {
		return results;
	}

	public void setResults(List<ResultItem> results) {
		this.results = results;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public static class ResultItem implements Serializable {
		private static final long serialVersionUID = 1L;
		private String result;
		private int resultKind;
		
		public ResultItem(String result, int kind) {
			this.result = result;
			this.resultKind = kind;
		}
		
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public int getResultKind() {
			return resultKind;
		}
		public void setResultKind(int resultKind) {
			this.resultKind = resultKind;
		}
	};
	
	
}
