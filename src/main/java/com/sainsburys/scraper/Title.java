package com.sainsburys.scraper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.sainsburys.exceptions.UnableToGetItemException;

public class Title implements ItemField{
	
	private final static Logger logger = LoggerFactory.getLogger(Title.class);

	
	DomElement product;
	Properties xpaths;
	
	public Title(DomElement product, Properties xpaths) {
		this.product = product;
		this.xpaths = xpaths;
	}
	
	@Override
	public String getField(DomElement product) {
		
		
		String title = getProductTitle(product);
		
		return title;
		
	}
	
	public String getProductTitle(DomElement product) {

		HtmlDivision prodNameAndLink = (HtmlDivision) product.getFirstByXPath(xpaths.getProperty("productNameAndLinkDivXpath"));
		
		if(prodNameAndLink == null) {
			
			logger.error("Unable to get the product name and link division, make sure the product name and link division exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the product name and link division");
			
		}
		
		HtmlAnchor an = (HtmlAnchor) prodNameAndLink.getFirstByXPath("h3/a");
		String title = an.asText();

		return title;
	}

}
