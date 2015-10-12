package com.mantralabsglobal.cashin.ai.businesscard.impl;

import com.mantralabsglobal.cashin.ai.businesscard.Extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class NameExtractor implements Extractor {

	public List<String> extract(List<String> lines, ContactInfo contact) {
		
		List<String> remainingLines = new ArrayList<String>();
		
		/*
		 * Loop backwards through the lines so we can remove
		 * them along the way.
		 */
		lineLoop: for(int i = lines.size() - 1; i >= 0; i--) {
			
			String line = lines.remove(i);
			
			boolean isName = isName(line, contact);
			
			if( isName ) {
				contact.setName(line);
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

	/**
	 * Use various means to determine if the entry is the contact's name
	 * @param line
	 * @param contact
	 * @return
	 */
	protected boolean isName(String line, ContactInfo contact) {
		
		// Does it contain a number? If so, not a name
		boolean hasNumber = containsNumbers(line);
		if(hasNumber) {
			return false;
		}
		
		boolean knownName = matchesNameGazetteer(line);
		if(knownName) {
			System.out.println("Known name: " + line);
			return true;
		}
		
		boolean emailContextCheck = checkEmailContext(line, contact);
		if(emailContextCheck) {
			System.out.println("Matched email: " + line);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Based on the first part of the email address (if available),
	 * check for character matches with a part of the name
	 * @param line
	 * @return
	 */
	protected boolean checkEmailContext(String line, ContactInfo contact) {
		String email = contact.getEmailAddress();
		// If no email was found, stop processing for context
		if(StringUtils.isBlank(email)) {
			return false;
		}
		else {
			/*
			 * Check to see if any tokens exist in the first part of the
			 * email address, where names usually are
			 */
			String emailFirst = StringUtils.substringBefore(email, "@").toLowerCase();
			
			String[] tokens = this.tokenizeLine(line);
			for(String token : tokens) {
				if(token.length()>=3 && emailFirst.contains(token.toLowerCase())) {
					System.out.println(emailFirst + " matched " + token);
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Returns true if the line contains a number
	 * @param line
	 * @return
	 */
	private boolean containsNumbers(String line) {
		return line.matches(".*(\\d)");
	}
	
	/**
	 * Do we know the words in here match known names?
	 * @param line
	 * @return
	 */
	protected boolean matchesNameGazetteer(String line) {
		String[] tokens = this.tokenizeLine(line);
		for(String token : tokens) {
			if(NAME_TOKENS.contains(token.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	
	private static final String[] NAME_GAZETTEER = {
		"john",
		"paul",
		"jones",
		"robert",
		"jimmy",
		"matthew",
		"mark"
		//TODO Load this from a gazetteer file with a fuller dictionary
	};
	
	private static final Set<String> NAME_TOKENS = new HashSet<String>();
	static {
		NAME_TOKENS.addAll(Arrays.asList(NAME_GAZETTEER));
	}
	
	/**
	 * Splits the line into word tokens for closer matching
	 * @param line
	 * @return
	 */
	private String[] tokenizeLine(String line) {
		return line.split("[^\\w']+");
	}
	
}
