package webscraper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sainsburys.exceptions.UnableToGetItemException;
import com.sainsburys.product.Item;
import com.sainsburys.scraper.FetchProductCallable;
import com.sainsburys.scraper.JSONItems;
import com.sainsburys.scraper.HtmlUnitItemScraper;

public class TestScraper {

	HtmlUnitItemScraper scraper = mock(HtmlUnitItemScraper.class);
	ObjectMapper mapper;
	WebClient webClient;
	String itemsPageurl;

	@Before
	public void setup() throws JsonParseException, JsonMappingException, IOException {
		
		
		File itemRelativePath = new File("items_page.html"); 
		
		 itemsPageurl = "file:///"+itemRelativePath.getAbsolutePath();
		 
		 
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

		webClient = new WebClient();
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		mapper = new ObjectMapper();

		String itemjson = " [\n" + "    {\n" + "      \"title\": \"Sainsbury's Strawberries 400g\",\n"
				+ "      \"kcal_per_100g\": 33,\n" + "      \"unit_price\": 1.75,\n"
				+ "      \"description\": \"by Sainsbury's strawberries\"\n" + "    },\n" + "    {\n"
				+ "      \"title\": \"Sainsbury's Blueberries 200g\",\n" + "      \"kcal_per_100g\": 45,\n"
				+ "      \"unit_price\": 1.75,\n" + "      \"description\": \"by Sainsbury's blueberries\"\n"
				+ "    },\n" + "    {\n" + "      \"title\": \"Sainsbury's Cherry Punnet 200g\",\n"
				+ "      \"kcal_per_100g\": 52,\n" + "      \"unit_price\": 1.5,\n"
				+ "      \"description\": \"Cherries\"\n" + "    }\n" + "\n" + "  ]";

		TypeFactory tf = mapper.getTypeFactory();

		List<Item> items = mapper.readValue(itemjson, tf.constructCollectionType(List.class, Item.class));

		when(scraper.getProductListings()).thenReturn(items);
	}

	@Test
	public void testTotal() throws JsonParseException, JsonMappingException, IOException {

		List<Item> items = scraper.getProductListings();

		JSONItems js = new JSONItems(items);

		BigDecimal total = js.getTotal();

		assertEquals("5.00", total.toPlainString());

	}


	@Test(expected = UnableToGetItemException.class)
	public void testXpath() throws FailingHttpStatusCodeException, MalformedURLException, IOException {


		FileInputStream fis = new FileInputStream("badXpath.props");
		Properties props = new Properties();

		props.load(fis);

		HtmlUnitItemScraper ws = new HtmlUnitItemScraper(itemsPageurl, props);

		HtmlPage itemsPage = webClient.getPage(itemsPageurl);

		ws.getProductList(itemsPage);

	}

	@Test
	public void testAProduct() throws Exception {
		
		
		
		
		String url = "";

		FileInputStream fis = new FileInputStream("xpath.props");
		Properties props = new Properties();

		props.load(fis);

		HtmlUnitItemScraper ws = new HtmlUnitItemScraper(itemsPageurl, props);

		HtmlPage page = webClient.getPage(itemsPageurl);

		Iterable<DomElement> listOfProds = ws.getProductList(page);

		Iterator<DomElement> prodIterator = listOfProds.iterator();

		DomElement item_7555699 = null;
		if (prodIterator.hasNext()) {

			item_7555699 = prodIterator.next();
		}

		FetchProductCallable prod = new FetchProductCallable(item_7555699, url, props);

		Item item = prod.call();

		String expectedItemJson = "{\n" + "  \"title\" : \"Sainsbury's Strawberries 400g\",\n"
				+ "  \"kcal_per_100g\" : 33,\n" + "  \"unit_price\" : 1.75,\n"
				+ "  \"description\" : \"by Sainsbury's strawberries\"\n" + "}";

		String itemJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);

		assertEquals(expectedItemJson, itemJson);

	}

}
