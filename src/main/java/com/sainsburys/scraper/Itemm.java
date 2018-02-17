package com.sainsburys.scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.sainsburys.exceptions.UnableToGetItemException;
import static com.sainsburys.scraper.Fields.*;
public class Itemm {
	
	

	private final static Logger logger = LoggerFactory.getLogger(HtmlUnitItemScraper.class);
	private final WebClient webClient;
	private DomElement product;
	String mainPageUrl;
	Properties xpaths;


	Map<String,ItemField> fields;
	
	public Itemm(DomElement product, Properties xpaths, String mainPageUrl) {
		this.fields = new HashMap<String,ItemField>();
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
		
		ItemField unit_price = new Description(product, xpaths,itemPage);

		
		
				
		fields.put(TITLE.toString(), title);
		fields.put(KCAL_PER_100G.name(), kcal_per_100kg);
		fields.put(DESCRIPTION.name(), description);
		fields.put(UNIT_PRICE.name(), unit_price);
		
		
		
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

	public void visit() {
		
	Set<String> itemFields  =fields.keySet();
	
	itemFields.stream().forEach(key ->{
		
		System.out.println(fields.get(key));
		
	});
		
	System.out.println("_________________");
	}
	
	public Itemm call()  {

		return this;
	}
	
	
}
