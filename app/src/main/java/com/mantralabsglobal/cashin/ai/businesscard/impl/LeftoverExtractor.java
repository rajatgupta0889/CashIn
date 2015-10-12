package com.mantralabsglobal.cashin.ai.businesscard.impl;

import com.mantralabsglobal.cashin.ai.businesscard.Extractor;

import java.util.List;


/**
 * In order to not lose any data,
 * put any remaining lines in the
 * 'other' field of the Contact
 * @author brian
 *
 */
public class LeftoverExtractor implements Extractor {

	public List<String> extract(List<String> lines, ContactInfo contact) {
		for(String line : lines) {
			contact.addOther(line);
		}
		return lines;
	}

}
