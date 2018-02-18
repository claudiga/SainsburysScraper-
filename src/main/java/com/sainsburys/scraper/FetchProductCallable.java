package com.sainsburys.scraper;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;

public class FetchProductCallable implements Callable<Item> {

	private final static Logger logger = LoggerFactory.getLogger(FetchProductCallable.class);

	private WebClient webClient;

	private ItemFetcher item;
	private ItemFieldVisitor visitor;

	public FetchProductCallable(DomElement product, String url, Properties xpaths, ItemFieldVisitor vistor) {

		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.item = new ItemFetcher(product, xpaths, url);
		this.visitor = vistor;
	}

	/**
	 * I would've used reused the getPage function from ____ but its not thread safe
	 * 
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

		this.item.getFields();

		this.item.accept(visitor);

		return visitor.getItem();

	}

}
