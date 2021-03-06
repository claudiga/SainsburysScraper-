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

public class HtmlUnitItemScraper implements ItemScraper<Item>{
	
	

	private final static Logger logger = LoggerFactory.getLogger(HtmlUnitItemScraper.class);
	private final String url;
	private final WebClient webClient;
	private Properties xpaths;
	private ItemFieldVisitor visitor;


	List<ItemFetcher> items;
	
	public HtmlUnitItemScraper(String url, Properties xpaths, ItemFieldVisitor visitor) {
		this.items = new ArrayList<ItemFetcher>();
		this.webClient = new WebClient();
		this.webClient.getOptions().setThrowExceptionOnScriptError(false);
		this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.url = url;
		this.xpaths = xpaths;
		this.visitor = visitor;
		
		
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
	
	public boolean checkXpath() {
		
		Predicate<String> p = xpaths::containsKey;
		
		List<String> properties = Arrays.asList("productListDivID","productListXpath","productNameAndLinkDivXpath","priceDivXpath","informationDivID","NutritionTable","descriptionDivXpath","energyTableRowXpath");
		
		int size = properties.stream().filter(p).collect(Collectors.toList()).size();
		
		return properties.size() == size;
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
	
	public List<Item> getProductListings() {

		ArrayList<Item> items = new ArrayList<>();
		HtmlPage page = getPage(url);

		Iterable<DomElement> listOfProds = getProductList(page);
		

		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<Future<Item>> results = new ArrayList<Future<Item>>();

		listOfProds.forEach(product -> {
			
			//If we want to use threads we must create new instance of a visitor for each thread to prevent race conditions
			ItemFieldVisitor visitorThreadinstance = null;
			try {
				visitorThreadinstance = visitor.getClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			Future<Item> reference = executor.submit(new FetchProductCallable(product, url, xpaths,visitorThreadinstance));

			results.add(reference);

		});
		
		if(results.isEmpty()) {
			
			logger.info("No items found... ");
		}
		
		for (Future<Item> itemFuture : results) {

			try {

				Item it = itemFuture.get();
				items.add(it);
			} catch (InterruptedException | ExecutionException e) {

				e.printStackTrace();
			}

		}
		
		executor.shutdown();
		while(!executor.isTerminated()) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {

			}
			executor.shutdownNow();
		}

		return items;
	}
	
}
