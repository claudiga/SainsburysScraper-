package com.sainsburys.scraper;

import com.sainsburys.fields.CaloriesPer100g;
import com.sainsburys.fields.Description;
import com.sainsburys.fields.Price;
import com.sainsburys.fields.Title;
import com.sainsburys.product.Item;

public interface ItemFieldVisitor {
	
	
	public void visit(Title title);
	public void visit(CaloriesPer100g Calories);
	public void visit(Description description);
	public void visit(Price price);
	public Item getItem();


}
