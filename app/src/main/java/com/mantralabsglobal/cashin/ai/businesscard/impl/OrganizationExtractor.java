package com.mantralabsglobal.cashin.ai.businesscard.impl;

import com.mantralabsglobal.cashin.ai.businesscard.Extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OrganizationExtractor implements Extractor {

	public List<String> extract(List<String> lines, ContactInfo contact) {
		
		List<String> remainingLines = new ArrayList<String>();
		
		/*
		 * Loop backwards through the lines so we can remove
		 * them along the way.
		 */
		lineLoop: for(int i = lines.size() - 1; i >= 0; i--) {
			
			String line = lines.remove(i);
			
			boolean isOrg = isOrganization(line);
			
			if( isOrg ) {
				contact.setOrganization(line);
				// Add all of the remaining lines since we found our match
				remainingLines.addAll(lines);
				break lineLoop;
			}
			else {
				remainingLines.add(line);
			}
		}
		
		return remainingLines;
		
	}

	private boolean isOrganization(String line) {
		String[] tokens = line.split("[^\\w']+");
		for(String token : tokens) {
			if(ORG_TOKENS.contains(token.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	
	private static final String[] ORG_GAZATEER = {
		"technologies",
		"inc",
		"llc",
		"incorporated",
		"limited liability corporation",
		"ltd"
		//TODO Load this from a gazateer file with a fuller dictionary
	};
	
	private static final Set<String> ORG_TOKENS = new HashSet<String>();
	static {
		ORG_TOKENS.addAll(Arrays.asList(ORG_GAZATEER));
	}
	
}
