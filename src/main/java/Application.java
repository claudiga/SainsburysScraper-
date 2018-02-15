import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.sainsburys.product.Item;
import com.sainsburys.scraper.JSONItems;
import com.sainsburys.scraper.HtmlUnitItemScraper;
import com.sainsburys.scraper.ItemScraper;

public class Application {
	


	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		

		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		
		
		String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
		
		String filePath = "xpath.props";
		
		FileInputStream fis = new FileInputStream(filePath);
		
		Properties xpaths = new Properties();
		
		xpaths.load(fis);
		
		ItemScraper<Item> wb = new HtmlUnitItemScraper(url, xpaths);
		
		List<Item> items = wb.getProductListings();
		
		JSONItems itemsJson = new JSONItems(items);
		
		String itemsJsonString  = itemsJson.getItemsAsJsonString();
	
		System.out.println(itemsJsonString);				
	}

}
