package com.sainsburys.scraper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

	public String getTotalAsString() {

		BigDecimal total = items.stream().map(Item::getUnit_price).reduce((e, y) -> {

			return e.add(y);

		}).get();

		total.setScale(2, RoundingMode.HALF_EVEN);

		return total.toPlainString();

	}

	public String getItemsAsJsonString() {

		ObjectNode obectNode = new ObjectNode(nodeFactory);
		JsonNode itemsNode = null;
		String output = null;
		String total = getTotalAsString();
		try {
			itemsNode = mapper.valueToTree(items);

			obectNode.set("results", itemsNode);

			obectNode.put("total", total);

			output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obectNode);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return output;

	}

}
