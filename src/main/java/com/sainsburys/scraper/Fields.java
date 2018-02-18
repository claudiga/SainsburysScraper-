package com.sainsburys.scraper;

public enum Fields {
	
	
	TITLE,KCAL_PER_100G,UNIT_PRICE,DESCRIPTION;
	
	public String toString() {
		
		
		return this.name().toLowerCase();
		
		
	}

}
