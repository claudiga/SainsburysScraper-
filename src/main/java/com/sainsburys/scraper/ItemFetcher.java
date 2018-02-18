package com.sainsburys.scraper;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.fields.CaloriesPer100g;
import com.sainsburys.fields.Description;
import com.sainsburys.fields.Price;
import com.sainsburys.fields.Title;

import static com.sainsburys.scraper.Fields.*;
public class ItemFetcher {
	
	

	private final static Logger logger = LoggerFactory.getLogger(HtmlUnitItemScraper.class);
	private final WebClient webClient;
	private DomElement product;
	private String mainPageUrl;
	private Properties xpaths;


	Map<String,ItemField> fields;
	
	public ItemFetcher(DomElement product, Properties xpaths, String mainPageUrl) {
		this.fields = new LinkedHashMap<String,ItemField>();
		this.webClient = new WebClient();
		this.webClient.getOptions().setThrowExceptionOnScriptError(false);
		this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.xpaths = xpaths;
		this.product = product;
		this.mainPageUrl = mainPageUrl;
		
	}
	
	public void getFields() {
		
		HtmlPage itemPage = getPage(getItemAbsolutePath());
		
		ItemField title = new Title(product, xpaths);
		
		ItemField kcal_per_100kg =  new CaloriesPer100g(product, xpaths,itemPage);
		
		ItemField description = new Description(product, xpaths,itemPage);
		
		ItemField unit_price = new Price(product, xpaths);

		
		fields.put(DESCRIPTION.toString(), description);
		fields.put(UNIT_PRICE.toString(), unit_price);		
		fields.put(KCAL_PER_100G.toString(), kcal_per_100kg);
		fields.put(TITLE.toString(), title);

	
	}
	
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
	
	public String getItemAbsolutePath() {

		HtmlDivision prodNameAndLink = (HtmlDivision) product.getFirstByXPath(xpaths.getProperty("productNameAndLinkDivXpath"));
		
		if(prodNameAndLink == null) {
			
			logger.error("Unable to get the product name and link division, make sure the product name and link division exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the product name and link division");
			
		}
		
		HtmlAnchor an = (HtmlAnchor) prodNameAndLink.getFirstByXPath("h3/a");
		String link = an.getAttribute("href");
		link = ScraperUtils.getAbsolutePath(mainPageUrl, link);
	

		return  link ;
	}

	public void accept(ItemFieldVisitor visitor) {
		
		Set<String> itemFields  =fields.keySet();
		
		itemFields.stream().forEach(key ->{
			
			ItemField field =fields.get(key);
			
			field.accept(visitor);
			
		});
		
		
		
	}
	
	

	
}
