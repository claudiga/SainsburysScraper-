package com.sainsburys.scraper;

import java.util.List;

import com.sainsburys.product.Item;

public interface ItemScraper<T,E> {
	
	public T getPage(String url) throws Exception;
	
	public Iterable<E> getProductList(T page); 
	
	public <Item extends Scrapeable> List<Item> getProductListings();
	
	

}
