import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sainsburys.product.Item;
import com.sainsburys.scraper.ItemsToJSON;
import com.sainsburys.scraper.HtmlUnitItemScraper;
import com.sainsburys.scraper.ItemFieldVisitor;
import com.sainsburys.scraper.ItemJsonFieldVisitor;
import com.sainsburys.scraper.ItemScraper;

public class Application {

	private final static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws IOException {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		if (args.length < 2 || args.length > 2) {
			logger.error("USAGE: java -jar webscraper-0.0.1-SNAPSHOT.jar [path to props] [products page url]");
			logger.info("props is at config/xpath.props   page url is https://jsainsburyplc.github.io/serverside-test"
					+ "/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html");
			System.exit(1);
		}

		new Application().run(args[0], args[1]);

	}

	public void run(String filePath, String url) throws IOException {

		ItemFieldVisitor visitor = new ItemJsonFieldVisitor();

		FileInputStream fis = new FileInputStream(filePath);

		Properties xpaths = new Properties();

		xpaths.load(fis);

		ItemScraper<Item> itemsScraper = new HtmlUnitItemScraper(url, xpaths, visitor);

		List<Item> items = itemsScraper.getProductListings();

		ItemsToJSON itemsJson = new ItemsToJSON(items);

		String itemsJsonString = itemsJson.getItemsAsJsonString();

		System.out.println(itemsJsonString);
	}

}
