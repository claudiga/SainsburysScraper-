package com.sainsburys.scraper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;

public class FetchProductCallable implements Callable<Item> {
	
	
	
	DomElement product;
		WebClient webClient;
		Logger logger;
		String url;
		Properties xpaths;
		
		
	public FetchProductCallable(DomElement product, Logger logger, String url, Properties xpaths) {
		
		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.logger = logger;
		this.product = product;
		this.url = url;
		this.xpaths = xpaths;
	}
	
	/**
	 * I would've used reused the getPage funtion from ____ but its not thread safe
	 * @param url
	 * @return
	 */
	public HtmlPage getPage(String url) {

		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
		} catch (Exception e) {
			logger.warn(String.format("unable to load the page at: %s ", url));
			throw new UnableToGetItemException("Unable to load page");

		}

		webClient.close();

		return page;

	}

	@Override
	public Item call() throws Exception {
	
		String[] productNameAndLink = getProductNameAndLink(product);

		String link = productNameAndLink[1];

		String itemName = productNameAndLink[0];

		String[] caloriesAndDescription = null;

		caloriesAndDescription = getCaloriesAndDescription(link);

		HtmlDivision priceDiv = (HtmlDivision) product.getFirstByXPath(xpaths.getProperty("priceDivXpath"));

		String unitPrice = priceDiv.asText().split("/")[0].replaceAll("[^0-9.]", "");

		System.out.println(unitPrice);

		Item item = new Item(itemName, Integer.parseInt(caloriesAndDescription[0]), new BigDecimal(unitPrice),
				caloriesAndDescription[1]);
		
		return item;
		
	}
	
	

	public String[] getProductNameAndLink(DomElement product) {

		HtmlDivision prodNameAndLink = (HtmlDivision) product.getFirstByXPath(xpaths.getProperty("productNameAndLinkDivXpath"));

		HtmlAnchor an = (HtmlAnchor) prodNameAndLink.getFirstByXPath("h3/a");
		String link = an.getAttribute("href");
		link = ScraperUtils.getAbsolutePath(url, link);
		String itemName = an.asText();

		return new String[] { itemName, link };
	}
	
	//@Override
	public String[] getCaloriesAndDescription(String url) {

		HtmlPage page = getPage(url);
		
		HtmlDivision info = (HtmlDivision) page.getElementById(xpaths.getProperty("informationDivID"));

		Optional<HtmlTable> tableOp = Optional
				.ofNullable((HtmlTable) info.getFirstByXPath(xpaths.getProperty("NutritionTable")));
		String calories = "-1";

		HtmlDivision description = (HtmlDivision) info
				.getFirstByXPath(xpaths.getProperty("descriptionDivXpath"));

		description = Optional.ofNullable(description).orElse((HtmlDivision) info.getFirstByXPath(
				"//*/div[@id='mainPart']/div[contains(@class,'itemTypeGroupContainer productText')]/div[contains(@class,'memo')]"));
		if (description == null) {
			description = info.getFirstByXPath(
					"//*/div[@id='mainPart']/div[contains(@class,'itemTypeGroupContainer productText')]/div[contains(@class,'itemTypeGroup')]");
		}

		if (tableOp.isPresent()) {
			HtmlTable nutritionTable = tableOp.get();
			HtmlTableRow tr = (HtmlTableRow) nutritionTable.getFirstByXPath(xpaths.getProperty("energyTableRowXpath"));
			String energy_per_kg = tr.getElementsByTagName("td").get(0).asText().replaceAll("[^0-9]", "");
			calories = ScraperUtils.kjToKcal.apply(energy_per_kg);
		} else {
			logger.info(String.format("Nutritional value for item at URL: %s is missing", url));
		}
		String descrip = "";
		String lineDescription = null;

		if (description != null) {
			descrip = description.asText();
			lineDescription = descrip.split("\n")[0];

		} else {

			logger.info(String.format("Description for item at URL: %s is missing.... Defaulting to empty string", url));

		}
		System.out.println(calories);

		System.out.println(lineDescription);

		return new String[] { calories, lineDescription };

	}

}