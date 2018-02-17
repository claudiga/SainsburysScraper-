package com.sainsburys.scraper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;

public class FetchProductCallable implements Callable<Itemm> {
	
	private final static Logger logger = LoggerFactory.getLogger(FetchProductCallable.class);
	
	DomElement product;
		WebClient webClient;
		String url;
		Properties xpaths;
		Itemm item;
		
		
	public FetchProductCallable(DomElement product, String url, Properties xpaths) {
		
		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.product = product;
		this.url = url;
		this.xpaths = xpaths;
		this.item = new Itemm(product, xpaths, url);
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
	public Itemm call() throws Exception {

		this.item.getFields();
		
		this.item.visit();
		
		return this.item;
		
	}
	

}
