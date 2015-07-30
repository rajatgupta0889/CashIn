package com.mantralabsglobal.cashin.ai.businesscard.impl;

import com.mantralabsglobal.cashin.ai.businesscard.Extractor;
import com.mantralabsglobal.cashin.ai.businesscard.ExtractorFactory;
import com.mantralabsglobal.cashin.ai.businesscard.IBusinessCardParser;
import com.mantralabsglobal.cashin.ai.businesscard.IContactInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class BusinessCardParserImpl implements IBusinessCardParser {

	private ExtractorFactory factory;
	
	public void setFactory(ExtractorFactory factory) {
		this.factory = factory;
	}
	
	public BusinessCardParserImpl() {
		this.factory = new ExtractorFactory();
	}

	/**
	 * Creates a pipeline of the extractors in a particular
	 * order. New pipelines could be identified and added as
	 * other methods if better context clues could be used.
	 * @return
	 */
	protected List<Extractor> createPipeline() {
		
		// Add the extractors into an ordered list
		List<Extractor> pipeline = new ArrayList<Extractor>();
		
		/*
		 * First, Email since it follows the most particular pattern
		 */
		pipeline.add(this.factory.createEmailExtractor());
		/*
		 * Second, Fax since if it exists, it will likely
		 * be prefixed with an indicator
		 */
		pipeline.add(this.factory.createFaxExtractor());
		/*
		 * Third, Phone because it's also easily identifiable
		 */
		pipeline.add(this.factory.createPhoneExtractor());
		/*
		 * Fourth, Organization because we can use org/company suffix
		 * clues, or possibly the host URL from the email address
		 */
		pipeline.add(this.factory.createOrganizationExtractor());
		/*
		 * Fifth, Name because it will be more easily extracted
		 * from a smaller data set
		 */
		pipeline.add(this.factory.createNameExtractor());
		/*
		 * Finally, collect any remaining lines into
		 * the 'other' field so we retain all input data
		 */
		pipeline.add(this.factory.createLeftoverExtractor());
		
		return pipeline;
	}
	
	/**
	 * Process the input contact lines into a Contact Info object
	 * @param document the raw input of non-classified contact information
	 * @return structured contact information extracted from the raw text input
	 */
	public IContactInfo getContactInfo( List<String> document) {
		
		// Pre-process the document into individual lines
		//List<String> contactLines = this.cleanInputLines(document);

		List<String> contactLines = document;
		// The object that will be modified during the pipeline
		ContactInfo contact = new ContactInfo();
		
		// Get the ordered extractors
		List<Extractor> pipeline = this.createPipeline();
		
		/*
		 * For now, we'll go sequentially, but here's where we'd
		 * handle parallelism if possible.
		 */
		for(Extractor extractor : pipeline) {
			/*
			 * Extract the input lines, then replace the variable
			 * with only what's left over
			 */
			contactLines = extractor.extract(contactLines, contact);
		}
		
		return contact;
		
	}
	
	/**
	 * Extract cleaned lines of text from the input
	 * @param input
	 * @return
	 */
	protected List<String> cleanInputLines(String input) {
		
		String[] inputLines = StringUtils.split(input, StringUtils.LF);
		List<String> cleanedLines = new ArrayList<String>();
		for(String rawLine : inputLines) {
			String cleanedLine = StringUtils.trimToNull(rawLine);
			if(cleanedLine != null) {
				cleanedLines.add(cleanedLine);
			}
		}
		return cleanedLines;
	}
	
}
