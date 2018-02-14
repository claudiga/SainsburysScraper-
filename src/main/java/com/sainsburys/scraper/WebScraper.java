package com.sainsburys.scraper;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;

public class WebScraper implements ItemScraper<HtmlPage, DomElement> {

	Logger logger = LoggerFactory.getLogger(getClass());
	final private String url;

	final private WebClient webClient;
	
	Properties xpaths;
	
	public WebScraper(String url, Properties xpaths) {
		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.url = url;
		this.xpaths = xpaths;

	}

	@Override
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
	public Iterable<DomElement> getProductList(HtmlPage page) {
		
		
		HtmlDivision productList = (HtmlDivision) page.getElementById("productLister");

		HtmlUnorderedList prods = (HtmlUnorderedList) productList
				.getFirstByXPath("//ul[contains(@class,'productLister gridView')]");

		Iterable<DomElement> listOfProds = prods.getChildElements();

		return listOfProds;
	}

	@Override
	public List<Item> getProductListings() {

		ArrayList<Item> items = new ArrayList<>();

		HtmlPage page = getPage(url);

		Iterable<DomElement> listOfProds = getProductList(page);

		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<Item>> results = new ArrayList<Future<Item>>();

		listOfProds.forEach(product -> {

			Future<Item> reference = executor.submit(new FetchProductCallable(product, logger, url));

			results.add(reference);

			// items.add(item);
			System.out.println("----------------------");
		});

		for (Future<Item> itemFuture : results) {

			try {

				Item it = itemFuture.get();
				items.add(it);
			} catch (InterruptedException | ExecutionException e) {

				e.printStackTrace();
			}

		}

		return items;
	}

}
