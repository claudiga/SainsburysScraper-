package com.sainsburys.fields;

import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.scraper.ItemField;
import com.sainsburys.scraper.ItemFieldVisitor;
import com.sainsburys.scraper.ScraperUtils;

public class CaloriesPer100g implements ItemField {
	
	private final static Logger logger = LoggerFactory.getLogger(CaloriesPer100g.class);

	
	DomElement product;
	Properties xpaths;
	HtmlPage itemPage;
	
	public CaloriesPer100g(DomElement product, Properties xpaths,HtmlPage itemPage) {
		this.product = product;
		this.xpaths = xpaths;
		this.itemPage = itemPage;
	}

	public int getField(DomElement product) {
		
		int calories = getCalories();
		
		return calories;
	}
	
	
	
	
	public int getCalories() {

		
		HtmlDivision info = (HtmlDivision) itemPage.getElementById(xpaths.getProperty("informationDivID"));

		if(info == null) {
			
			logger.error("Unable to get the informtion division, make sure the informtion division exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the informtion division");
		}
		
		Optional<HtmlTable> tableOp = Optional
				.ofNullable((HtmlTable) info.getFirstByXPath(xpaths.getProperty("NutritionTable")));
		String calories = "-1";


		if (tableOp.isPresent()) {
			HtmlTable nutritionTable = tableOp.get();
			HtmlTableRow tr = (HtmlTableRow) nutritionTable.getFirstByXPath(xpaths.getProperty("energyTableRowXpath"));
			String energy_per_kg = tr.getElementsByTagName("td").get(0).asText().replaceAll("[^0-9]", "");
			calories = ScraperUtils.kjToKcal.apply(energy_per_kg);
		} else {
			logger.info(String.format("Nutritional value for item at URL: %s is missing...kcal_per_100g field has been omitted", itemPage.getBaseURL()));
		}

		return Integer.parseInt(calories);

	}

	@Override
	public void accept(ItemFieldVisitor visitor) {

		visitor.visit(this);
	}
	
}
