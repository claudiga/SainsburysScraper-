package com.sainsburys.scraper;

import java.util.List;

public interface ItemScraper<T extends Scrapeable> {
			
	public  List<T> getProductListings();
		

}
