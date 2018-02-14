package com.sainsburys.scraper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	
	public String getItemsAsJsonString() {
		
		String json = null;
		ObjectNode obectNode = new ObjectNode(nodeFactory);
		 
		 String output = null;
		 
		try {
			json = mapper.writeValueAsString(items);
			
			 obectNode.put("results", json);
			 
			 obectNode.put("total", "5");
			 
			output = mapper.writeValueAsString(obectNode);

	
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
		
	}
	
	

	
	

}
