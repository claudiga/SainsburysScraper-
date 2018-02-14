import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.sainsburys.scraper.WebScraper;

public class Application {
	


	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		

		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		
		String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
		WebScraper wb = new WebScraper(url);
		
		//wb.getProductListings();
		
		wb.getCaloriesAndDescription("https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/berries-cherries-currants/sainsburys-cherry-punnet-200g-468015-p-44.html");
		
	}

}
