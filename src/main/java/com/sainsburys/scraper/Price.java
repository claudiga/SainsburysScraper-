package com.sainsburys.scraper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.sainsburys.exceptions.UnableToGetItemException;

public class Price implements ItemField {

	private final static Logger logger = LoggerFactory.getLogger(CaloriesPer100g.class);
	DomElement product;
	Properties xpaths;

	public Price(DomElement product, Properties xpaths) {

		this.product = product;

	}

	public String getUnitPrice() {

		HtmlDivision priceDiv = (HtmlDivision) product.getFirstByXPath(xpaths.getProperty("priceDivXpath"));

		if (priceDiv == null) {

			logger.error(
					"Unable to get the price division, make sure the price division exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the price div");
		}
		String unitPrice = priceDiv.asText().split("/")[0].replaceAll("[^0-9.]", "");

		return unitPrice;
	}

	@Override
	public String getField(DomElement product) {
		String unitPrice = getUnitPrice();

		return unitPrice;
	}

}
