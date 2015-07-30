package com.mantralabsglobal.cashin.ai.businesscard.impl.phone;

import org.apache.commons.lang3.StringUtils;

public class PhoneFormatter {

	public static String formatPhoneNumber(String numberLine) {
		String first = StringUtils.substring(numberLine, 0, 3);
		String second = StringUtils.substring(numberLine, 3, 6);
		String third = StringUtils.substring(numberLine, 6, 10);
		
		String phoneOutput = String.format("(%s) %s-%s", first, second, third);
		
		//TODO Should we handle/include extensions?
		String extension = StringUtils.substring(numberLine, 10);
		if(StringUtils.isNotEmpty(extension)) {
			phoneOutput += " x" + extension;
		}
		
		return phoneOutput;
		
	}
	
}
