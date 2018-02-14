package com.sainsburys.product;

import java.math.BigDecimal;

import com.sainsburys.scraper.Scrapeable;

public class Item implements Scrapeable{

	private String title;
	private int kcal_per_100g;
	private BigDecimal unit_price;
	private String description;

	public Item(String title, int kcal_per_100g, BigDecimal unit_price, String description) {

		this.title = title;

		this.kcal_per_100g = kcal_per_100g;

		this.unit_price = unit_price;

		this.description = description;

	}

	public String getTitle() {
		return title;
	}

	public int getKcal_per_100g() {
		return kcal_per_100g;
	}

	public void setKcal_per_100g(int kcal_per_100g) {
		this.kcal_per_100g = kcal_per_100g;
	}

	public BigDecimal getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

	public String getDescription() {
		return description;
	}


}
