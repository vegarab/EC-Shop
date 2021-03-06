package org.web.server.serializers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.web.server.AbstractServlet.HTTPMethod;
import org.web.server.persistance.Persister;
import org.web.server.persistance.Persister.ModelClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr1864.app.core.Action;
import tdt4140.gr1864.app.core.Coordinate;
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
	public int deserialize(BufferedReader reader, Class c, HTTPMethod method) {
		
		/* For classes that needs more advanced serializing */
		switch(ModelClasses.fromClass(c)) {
			case SHOPPING_TRIP: object = deserializeShoppingTrip(reader); break;
			default: object = genericDeserialize(reader, c);break;
		}
		return persister.persist(object, c, method);
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
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	public String serialize(Object o, Class c) throws IOException {
		/* For classes that needs more advanced serializing */
		switch(ModelClasses.fromClass(c)) {
			case RECEIPT: return serializeReceipt(o); // We deliberately do not cast here; it is handled in the method itself
			case SHOPPING_TRIP: return serializeShoppingTrip(o);
			case PRODUCTAMOUNT: return serializeProductAmount(o, c);
			default:
				try {
					return serialize((Model) o, c);
				} catch (ClassCastException e) {
					break;
				}
		}
		return null;
	}

	private String serializeProductAmount(Object o, Class c) {
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
	 * Checks if incoming object is a list or single Trip, serializes based on that
	 * @param object 	List<ShoppingTrip> or ShoppingTrip to be serialized
	 * @return	JSON String
	 */
	@SuppressWarnings({ "null", "unchecked" })
	private String serializeShoppingTrip(Object object) {
		if (object instanceof List<?>) return serializeShoppingTrips((List<ShoppingTrip>) object);
		ShoppingTrip trip = (ShoppingTrip) object;
		
		return serializeShoppingTrip(trip).toJSONString();
	}
	
	/**
	 * Serializes a single ShoppingTrip, returns JSONObject
	 * @param trip 		ShoppingTrip to be serialized
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	private JSONObject serializeShoppingTrip(ShoppingTrip trip) {
		// Trip object
		JSONObject jTrip = new JSONObject();
		
		// customerID in Trip
		jTrip.put("customerID", trip.getCustomer().getID());
		
		// Coordinates
		JSONArray jCoordinates = serializeCoordinates(trip);
		jTrip.put("coordinates", jCoordinates);
		// Actions
		JSONArray jActions = serializeActions(trip);
		jTrip.put("actions", jActions);

		return jTrip;
	}
	
	/**
	 * Serializes a list of ShoppingTrips to JSON
	 * @param trips		List of Trips to be serialized
	 * @return JSON String
	 */
	@SuppressWarnings("unchecked")
	private String serializeShoppingTrips(List<ShoppingTrip> trips) {
		JSONArray jRoot = new JSONArray();
		
		for (ShoppingTrip trip : trips) {
			jRoot.add(serializeShoppingTrip(trip));
		}
		return jRoot.toJSONString();
	}

	/**
	 * Serializes Coordinates based on ShoppingTrip
	 * @param trip		trip to serialize Coordinates from
	 * @return JSON String
	 */
	@SuppressWarnings({ "null", "unchecked" })
	private JSONArray serializeCoordinates(ShoppingTrip trip) {
		JSONArray jCoordinates = new JSONArray();
		JSONObject jCoordinate = new JSONObject();
		
		for (Coordinate coordinate : trip.getCoordinates()) {
			jCoordinate = new JSONObject();
			jCoordinate.put("x", coordinate.getX());
			jCoordinate.put("y", coordinate.getY());
			jCoordinate.put("timestamp", coordinate.getTimeStamp());
			jCoordinates.add(jCoordinate);
		}
		return jCoordinates;
	}
	
	/**
	 * Serializes Actions based on ShoppingTrip
	 * @param trip		trip to serialize Actions form
	 * @return	JSON String
	 */
	@SuppressWarnings({ "null", "unchecked" })
	private JSONArray serializeActions(ShoppingTrip trip) {
		JSONArray jActions = new JSONArray();
		JSONObject jAction = new JSONObject();
		
		for (Action action : trip.getActions()) {
			jAction = new JSONObject();
			jAction.put("timestamp", action.getTimeStamp());
			jAction.put("action", action.getActionType());
			jAction.put("productID", action.getProduct().getID());
			jActions.add(jAction);
		}
		return jActions;
	}

	private String serializeReceipt(Object object) {
		if (!(object instanceof Receipt) && !(object instanceof List<?>)) {
			throw new IllegalArgumentException("Argument passed to serializeReceipt was not a receipt or a list of receipts.");
		}
		
		ObjectMapper mapper = new ObjectMapper();

		SimpleModule module = new SimpleModule("ReceiptSerializer", new Version(1, 0, 0, null, null, null));
		module.addSerializer(Receipt.class, new ReceiptSerializer());
		mapper.registerModule(module);

		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

		try {
			return writer.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

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

	/* Used by tests */
	public Object getObject() {
		return object;
	}
}
