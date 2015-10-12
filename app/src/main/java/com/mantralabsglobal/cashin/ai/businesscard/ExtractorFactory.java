package com.mantralabsglobal.cashin.ai.businesscard;

import com.mantralabsglobal.cashin.ai.businesscard.impl.EmailExtractor;
import com.mantralabsglobal.cashin.ai.businesscard.impl.FaxExtractor;
import com.mantralabsglobal.cashin.ai.businesscard.impl.LeftoverExtractor;
import com.mantralabsglobal.cashin.ai.businesscard.impl.NameExtractor;
import com.mantralabsglobal.cashin.ai.businesscard.impl.OrganizationExtractor;
import com.mantralabsglobal.cashin.ai.businesscard.impl.PhoneExtractor;

public class ExtractorFactory {

	public Extractor createEmailExtractor() {
		return new EmailExtractor();
	}
	
	public Extractor createNameExtractor() {
		return new NameExtractor();
	}
	
	public Extractor createOrganizationExtractor() {
		return new OrganizationExtractor();
	}
	
	public Extractor createPhoneExtractor() {
		return new PhoneExtractor();
	}
	
	public Extractor createFaxExtractor() {
		return new FaxExtractor();
	}
	
	public Extractor createLeftoverExtractor() {
		return new LeftoverExtractor();
	}
	
}
