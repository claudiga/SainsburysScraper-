package com.sainsburys.scraper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.sainsburys.product.Item;

public class WebScraper implements ItemScraper<HtmlPage,DomElement> {
	Logger logger = LoggerFactory.getLogger(getClass());
	final private ObjectMapper mapper;
	final private  String url;

	final private WebClient webClient;

	public WebScraper(String url) {
		mapper = new ObjectMapper();
		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.url = url;
		
	}
	
	@Override
	public HtmlPage getPage(String url){

		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
		} catch (Exception e) {
	
		}

		webClient.close();

		return page;

	}
	
	@Override
	public Iterable<DomElement> getProductList(HtmlPage page){
		
		HtmlDivision productList = (HtmlDivision) page.getElementById("productLister");

		HtmlUnorderedList prods = (HtmlUnorderedList) productList
				.getFirstByXPath("//ul[contains(@class,'productLister gridView')]");

		Iterable<DomElement> listOfProds = prods.getChildElements();
		
		return listOfProds;
	}
	
	public String[] getProductNameAndLink(DomElement product) {
		
		HtmlDivision prodNameAndLink = (HtmlDivision) product.getFirstByXPath(
				"div[contains(@class,'product')]/div[contains(@class,'productInfo')]/div[contains(@class,'productNameAndPromotions')]");

		HtmlAnchor an = (HtmlAnchor) prodNameAndLink.getFirstByXPath("h3/a");
		String link = an.getAttribute("href");
		link = ScraperUtils.getAbsolutePath(url, link);
		String itemName = an.asText();
		
		return new String[]{itemName,link};
	}
	
	@Override
	public List<Item> getProductListings(){

		ArrayList<Item> items = new ArrayList<>();

		HtmlPage page = getPage(url);

		HtmlDivision productList = (HtmlDivision) page.getElementById("productLister");

		HtmlUnorderedList prods = (HtmlUnorderedList) productList
				.getFirstByXPath("//ul[contains(@class,'productLister gridView')]");

		Iterable<DomElement> listOfProds = getProductList(page);

		int counter = 0;
		
		listOfProds.forEach(product -> {

			
			String[] productNameAndLink = getProductNameAndLink(product);
					
			String link =  productNameAndLink[1];
			
			String itemName = productNameAndLink[0];
			
			String[] caloriesAndDescription = null;
			
			try {
				caloriesAndDescription = getCaloriesAndDescription(link);

			} catch (FailingHttpStatusCodeException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			

			HtmlDivision priceDiv = (HtmlDivision) product.getFirstByXPath(".//div[starts-with(@id,'addItem_')]");

			String unitPrice = priceDiv.asText().split("/")[0].replaceAll("[^0-9.]", "");

			System.out.println(unitPrice);

			Item item = new Item(itemName, Integer.parseInt(caloriesAndDescription[0]), new BigDecimal(unitPrice),
					caloriesAndDescription[1]);

			items.add(item);
			System.out.println("----------------------");
		});

		//String itemsJ = mapper.writeValueAsString(items);
		//System.out.println(itemsJ);
		return items;
	}
	
	

	public String[] getCaloriesAndDescription(String url)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = webClient.getPage(url);

		HtmlDivision info = (HtmlDivision) page.getElementById("information");
		
		Optional<HtmlTable> tableOp = Optional.ofNullable((HtmlTable) info.getFirstByXPath("//*/table[contains(@class,'nutritionTable')]"));
		String calories = "-1";
		
		
	
		
		HtmlDivision description = (HtmlDivision) info
				.getFirstByXPath("productcontent/htmlcontent/div[contains(@class,'productText')]");
		
		description = Optional.ofNullable(description).orElse( (HtmlDivision) info.getFirstByXPath("//*/div[@id='mainPart']/div[contains(@class,'itemTypeGroupContainer productText')]/div[contains(@class,'memo')]"));

		
		if(tableOp.isPresent()) {
			HtmlTable nutritionTable = tableOp.get();
			HtmlTableRow tr = (HtmlTableRow) nutritionTable.getFirstByXPath("//*/tr[th[starts-with(.,'Energy')]]");
			String energy_per_kg = tr.getElementsByTagName("td").get(0).asText().replaceAll("[^0-9]", "");
			calories = ScraperUtils.kjToKcal.apply(energy_per_kg);
		}
		String descrip  = null;
		String lineDescription = null;
		
		if(description != null)
		{
			descrip = description.asText();
			lineDescription = descrip.split("\n")[0];
			
		}else {
			
			
		}	
		System.out.println(calories); 

	
		System.out.println(lineDescription);

		return new String[] { calories, lineDescription };

	}

}
