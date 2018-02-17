import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sainsburys.product.Item;
import com.sainsburys.scraper.JSONItems;
import com.sainsburys.scraper.HtmlUnitItemScraper;
import com.sainsburys.scraper.ItemScraper;
import com.sainsburys.scraper.Itemm;
import com.sainsburys.scraper.Items;

public class Application {
	
	private final static Logger logger = LoggerFactory.getLogger(Application.class);


	public static void main(String[] args) throws IOException {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 

		if(args.length < 2 || args.length > 2) {
			logger.error("USAGE: java -jar webscraper-0.0.1-SNAPSHOT.jar [path to props] [products page url]");
			System.exit(1);
		}
		
		new Application().visit(args[0],args[1]);
		
			
	}
	
	public void run(String filePath, String url) throws IOException {
				
		FileInputStream fis = new FileInputStream(filePath);
		
		Properties xpaths = new Properties();
		
		xpaths.load(fis);
		
		ItemScraper<Item> wb = new HtmlUnitItemScraper(url, xpaths);
		
		List<Item> items = wb.getProductListings();
		
		JSONItems itemsJson = new JSONItems(items);
		
		String itemsJsonString  = itemsJson.getItemsAsJsonString();
	
		System.out.println(itemsJsonString);		
		
	}
	
	
	public void visit(String filePath, String url) throws IOException {
		
FileInputStream fis = new FileInputStream(filePath);
		
		Properties xpaths = new Properties();
		
		xpaths.load(fis);
		
		Items items = new Items(url, xpaths);
		
		List<Itemm> itemss = items.getProductListings();
		
		System.out.println(itemss);
		
	}
	

}
