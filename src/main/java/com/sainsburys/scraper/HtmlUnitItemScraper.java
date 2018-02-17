package com.sainsburys.scraper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;

public class HtmlUnitItemScraper implements ItemScraper<Item> {

	private final static Logger logger = LoggerFactory.getLogger(HtmlUnitItemScraper.class);
	private final String url;
	private final WebClient webClient;
	
	Properties xpaths;
	
	public HtmlUnitItemScraper(String url, Properties xpaths) {
		this.webClient = new WebClient();
		this.webClient.getOptions().setThrowExceptionOnScriptError(false);
		this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.url = url;
		this.xpaths = xpaths;

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

	public Iterable<DomElement> getProductList(HtmlPage page) {
		
		if(!checkXpath()) {
			
			throw new UnableToGetItemException("An xpath property is missing");
		}
		
		HtmlDivision productList = (HtmlDivision) page.getElementById(xpaths.getProperty("productListDivID"));

		if(productList == null) {
			
			logger.error("Unable to get the product list Division make sure the div holding the product list is correct in props.... exiting");
			throw new UnableToGetItemException("Unable to get the product list Division");
		}
		
		HtmlUnorderedList prods = (HtmlUnorderedList) productList
				.getFirstByXPath(xpaths.getProperty("productListXpath"));
		
		if(prods == null) {
			
			logger.error("Unable to get the product list, make sure the product list exist or is in the props... exiting");
			throw new UnableToGetItemException("Unable to get the product list");
		}
		Iterable<DomElement> listOfProds = prods.getChildElements();

		return listOfProds;
	}
	
	/**
	 * better to fail fast then fail later when we realise a property thats needed doesn't exist
	 */
	public boolean checkXpath() {
		
		Predicate<String> p = xpaths::containsKey;
		
		List<String> properties = Arrays.asList("productListDivID","productListXpath","productNameAndLinkDivXpath","priceDivXpath","informationDivID","NutritionTable","descriptionDivXpath","energyTableRowXpath");
		
		int size = properties.stream().filter(p).collect(Collectors.toList()).size();
		
		return properties.size() == size;
	}
	/**
	 * We split up the work so that if one item fails to be scraped for some reason we can
	 * return partial list of items if its acceptable.
	 */


	@Override
	public List<Item> getProductListings() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
