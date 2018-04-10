package org.web.server.serializers;

import java.io.BufferedReader;
import java.io.IOException;

import org.web.server.AbstractServlet.HTTPMethod;
import org.web.server.persistance.Persister;
import org.web.server.persistance.Persister.ModelClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tdt4140.gr1864.app.core.Receipt;
import tdt4140.gr1864.app.core.ShoppingTrip;
import tdt4140.gr1864.app.core.database.DataLoader;
import tdt4140.gr1864.app.core.interfaces.Model;

/**
 * Generic (de)serializer for web.server
 * @author vegarab
 */
public class Serializer {

	/* global objects used for tests */
	private Object object;
	
	/* Used for singleton design */
	private static Serializer serializer;
	
	/* Used for persisting objects to/from database */
	private Persister persister = Persister.init();

	/**
	 * Private constructor for singleton design
	 */
	private Serializer() {}
	
	/**
	 * Retrieves a Serializer object based on singleton design-pattern
	 * @return Serializer object
	 */
	public static Serializer init() {
		if (serializer == null) {
			serializer = new Serializer();
		}
		return serializer;
	}
	
	
	/**
	 * Deserializes JSON data from reader into POJO. Does handle non-POJO models 
	 * based on the objects class. Further calls functionality in persistence-layer 
	 * through Persister-class based on method
	 * @param reader 	BufferedReader with JSON-data
	 * @param c			Class of object to deserialize to
	 * @param method 	HTTP method
	 */
	@SuppressWarnings({ "rawtypes" })
	public void deserialize(BufferedReader reader, Class c, HTTPMethod method) {
		
		/* For classes that needs more advanced serializing */
		switch(ModelClasses.fromClass(c)) {
			case RECEIPT: object = deserializeReceipt(reader); break;
			case SHOPPING_TRIP: object = deserializeShoppingTrip(reader); break;
			default: object = genericDeserialize(reader, c);break;
		}
		persister.persist(object, c, method);
	}
	
	/**
	 * Generic deserializer that can serialize POJOs with Jackson annotations
	 * @param reader 	BufferedReader with JSON-data
	 * @param c			Class of object to serialize to 
	 * @return object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Model genericDeserialize(BufferedReader reader, Class c) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			object = (Model) mapper.readValue(reader, c);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (Model) object;
	}
	
	/**
	 * Serializes POJO into JSON. 
	 * @param o 	Model object to serialize
	 * @param c    	Class of object
	 * @return JSON String
	 */
	@SuppressWarnings("rawtypes")
	private String serialize(Model o, Class c) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * Serializer that handles classes that requires more advanced
	 * serializing
	 * @param o		objcet to serialize
	 * @param c		Class of object
	 * @return JSON String
	 */
	@SuppressWarnings("rawtypes")
	public String serialize(Object o, Class c) {
		/* For classes that needs more advanced serializing */
		switch(ModelClasses.fromClass(c)) {
			case RECEIPT: return serializeReceipt((Receipt) o);
			case SHOPPING_TRIP: return serializeShoppingTrip((ShoppingTrip) o);
			default:
				try {
					return serialize((Model) o, c);
				} catch (ClassCastException e) {
					break;
				}
		}
		return null;
	}

	private String serializeShoppingTrip(ShoppingTrip trip) {
		// TODO Auto-generated method stub
		return null;
	}

	private String serializeReceipt(Receipt receipt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Calls functions to deserialize ShoppingTrip with its trips and actions.
	 * app.core's DataLoader has this functionality, so it is used.
	 * @param reader	BufferedReader with JSON-data
	 */
	private Model deserializeShoppingTrip(BufferedReader reader) {
		String json = new String();
		try {
			for (String line; (line = reader.readLine()) != null; json += line);
		} catch (IOException e) { e.printStackTrace(); }

		/* Properly persists JSON to Trips/Coordinates/Actions */
		ShoppingTrip trip = DataLoader.loadShoppingTrip(json);
		return trip;
	}

	private Object deserializeReceipt(BufferedReader reader) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* Used by tests */
	public Object getObject() {
		return object;
	}
}
