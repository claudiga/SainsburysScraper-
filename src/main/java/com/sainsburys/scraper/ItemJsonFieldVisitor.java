package com.sainsburys.scraper;

import java.math.BigDecimal;
import com.sainsburys.fields.CaloriesPer100g;
import com.sainsburys.fields.Description;
import com.sainsburys.fields.Price;
import com.sainsburys.fields.Title;
import com.sainsburys.product.Item;

public class ItemJsonFieldVisitor implements ItemFieldVisitor {

	private Item item;
	
	 public ItemJsonFieldVisitor() {
		item = new Item();
	}

	@Override
	public void visit(Title title) {
		String itemTitle = title.getProductTitle();
		
		item.setTitle(itemTitle);

	}

	@Override
	public void visit(CaloriesPer100g calories) {

		int itemCaloriesPer100g = calories.getCalories();
		
		item.setKcal_per_100g(itemCaloriesPer100g);


	}

	@Override
	public void visit(Description description) {

		String itemDescription = description.getDescription();
		
		item.setDescription(itemDescription);

	}

	@Override
	public void visit(Price price) {

		BigDecimal itemPrice = price.getUnitPrice();
		
		item.setUnit_price(itemPrice);
	}
	
	@Override
	public Item getItem() {

		return this.item;

	}

}
