import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.sainsburys.product.Item;
import com.sainsburys.scraper.JSONItems;
import com.sainsburys.scraper.WebScraper;

public class Application {
	


	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		

		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		
		
		String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
		
		String filePath = "xpath.props";
		
		FileInputStream fis = new FileInputStream(filePath);
		
		Properties xpaths = new Properties();
		
		xpaths.load(fis);
		
		WebScraper wb = new WebScraper(url, xpaths);
		
		List<Item> items = wb.getProductListings();
		
		JSONItems itemsJson = new JSONItems(items);
		
		String itemsJsonString  = itemsJson.getItemsAsJsonString();
	
		System.out.println(itemsJsonString);
				
		
		
		
		//wb.getCaloriesAndDescription("https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/berries-cherries-currants/sainsburys-blackcurrants-150g.html");
		
	}

}
