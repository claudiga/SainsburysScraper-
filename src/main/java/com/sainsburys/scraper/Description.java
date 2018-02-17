package com.sainsburys.scraper;

import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.sainsburys.exceptions.UnableToGetItemException;

public class Description implements ItemField{
	
	private final static Logger logger = LoggerFactory.getLogger(CaloriesPer100g.class);

	
	DomElement product;
	Properties xpaths;
	HtmlPage itemPage;
	
	public Description(DomElement product, Properties xpaths,HtmlPage itemPage) {
		this.product = product;
		this.xpaths = xpaths;
		this.itemPage = itemPage;
	}

	@Override
	public String getField(DomElement product) {

		String description = getDescription();
		
		return description;
	}
	
	public String getDescription() {


		
		HtmlDivision info = (HtmlDivision) itemPage.getElementById(xpaths.getProperty("informationDivID"));

		if(info == null) {
			
			logger.error("Unable to get the informtion division, make sure the informtion division exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the informtion division");
		}
		

		
		HtmlDivision description = (HtmlDivision) info
				.getFirstByXPath(xpaths.getProperty("descriptionDivXpath"));

		description = Optional.ofNullable(description).orElse((HtmlDivision) info.getFirstByXPath(
				"//*/div[@id='mainPart']/div[contains(@class,'itemTypeGroupContainer productText')]/div[contains(@class,'memo')]"));
		if (description == null) {
			description = info.getFirstByXPath(
					"//*/div[@id='mainPart']/div[contains(@class,'itemTypeGroupContainer productText')]/div[contains(@class,'itemTypeGroup')]");
		}

		String descrip = "";
		String lineDescription = null;

		if (description != null) {
			descrip = description.asText();
			lineDescription = descrip.split("\n")[0];

		} else {

			logger.info(String.format("Description for item at URL: %s is missing.... Defaulting to empty string", itemPage.getBaseURI()));

		}
		

		return lineDescription ;

	}

}
