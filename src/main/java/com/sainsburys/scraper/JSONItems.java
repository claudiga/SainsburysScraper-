package com.sainsburys.scraper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.sainsburys.product.Item;

public class JSONItems {

	final private ObjectMapper mapper;
	final private JsonNodeFactory nodeFactory;

	List<Item> items;

	public JSONItems(List<Item> items) {
		this.mapper = new ObjectMapper();
		this.items = items;
		nodeFactory = JsonNodeFactory.instance;

	}

	public BigDecimal getTotal() {

		BigDecimal total = items.stream().map(Item::getUnit_price).reduce((e, y) -> {

			return e.add(y);

		}).get();

		total.setScale(2, RoundingMode.HALF_EVEN);

		return total;

	}

	public String getItemsAsJsonString() {

		ObjectNode objectNode = new ObjectNode(nodeFactory);
		JsonNode itemsNode = null;
		String output = null;
		BigDecimal total = getTotal();
		try {
			itemsNode = mapper.valueToTree(items);
			
			Iterator<JsonNode> it = itemsNode.iterator();
			
			while(it.hasNext()) {
				
				JsonNode j = it.next();
				
				JsonNode k = j.get("kcal_per_100g");
						
				if(k.intValue() == -1) {
					
					ObjectNode o = (ObjectNode) j;
					
					o.remove("kcal_per_100g");
				}
				
			}

			objectNode.set("results", itemsNode);
			
			RawValue rv = new RawValue(total.toPlainString());
			
			objectNode.putRawValue("total", rv);
			
			
			
			output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return output;

	}

}
