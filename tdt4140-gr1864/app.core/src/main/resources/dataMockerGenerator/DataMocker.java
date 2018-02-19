package dataMockerGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DataMocker {
	/**
	 * A Rectangle containing the start and end of all paths. Typically the cashier desks.
	 */
	private Rectangle home;
	
	/**
	 * Zones where a customer may stop to pick up goods. We use a List instead of a collection
	 * so that we can easily shuffle the zones in order to pick a random subset. 
	 */
	private List<Rack> zones;
	
	/**
	 * @param home A rectangle containing the cashier desks.
	 * @param zones A collection of zones where the customer may stop to pick up goods.
	 */
	public DataMocker(Rectangle home, List<Rack> zones) {
		this.home = home;
		this.zones = zones;
	}
	
	/**
	 * Generates a random coordinate inside a given rectangle.
	 * @param rectangle
	 * @return A random coordinate contained within the given rectangle.
	 */
	private Coordinate generateRandomCoordinateInsideRectangle(Rectangle rectangle) {
		return new Coordinate(
					ThreadLocalRandom.current().nextDouble(rectangle.getLower().getX(), rectangle.getUpper().getX()),
					ThreadLocalRandom.current().nextDouble(rectangle.getLower().getY(), rectangle.getUpper().getY())
				);
	}
	
	/**
	 * Returns a coordinate on the line connecting the start and end coordinates.
	 * @param start The start coordinate.
	 * @param end The end coordinate.
	 * @param displacement The position on the line, given as a percentage between 0 and 1.
	 * @return A coordinate on the line connecting the start and end coordinates.
	 */
	private Coordinate getPointAtSlopeBetweenCoordinates(Coordinate start, Coordinate end, double displacement) {
		double position = start.getX() + (end.getX() - start.getX()) * displacement;
		double slope = (end.getY() - start.getY()) / (end.getX() - start.getX());
		return new Coordinate(position, start.getY() + slope * (position - start.getX()));
	}
	
	/**
	 * Returns a list containing evenly points on the line connecting the start and end coordinates.
	 * @param start The start coordinate.
	 * @param end The end coordinate.
	 * @param intensity The distance between samples.
	 * @param fuzz The amount of randomness added to samples.
	 * @return A list of distinct coordinate on the line connecting the start and end coordinates.
	 */
	private List<Coordinate> getAllPointsAtSlopeBetweenCoordinates(Coordinate start, Coordinate end, double intensity, double fuzz) {
		List<Coordinate> coordinates = new ArrayList<>();
		
		// Calculate the distance between the start and end coordinates.
		double distance = Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
		
		for (double i = 0; i <= distance; i += intensity) {
			Coordinate position = getPointAtSlopeBetweenCoordinates(start, end, i / distance);
			
			// Add some noise to the coordinates.
			position.setX(position.getX() + ThreadLocalRandom.current().nextDouble(-fuzz / 2, fuzz / 2));
			position.setY(position.getY() + ThreadLocalRandom.current().nextDouble(-fuzz / 2, fuzz / 2));
			
			coordinates.add(position);
		}
		
		return coordinates;
	}
	
	/**
	 * Generate a sequence of coordinates describing an users path through the shop.
	 * UNIT: five units is one meter, maximum speed is 60 units per second
	 * @param intensity The distance between samples.
	 * @param fuzz The amount of randomness added to samples.
	 * @param speed The number of units the person walks each second.
	 * @return A list of coordinates between a random set of zones, starting and ending in the home zone, and'
	 * a list of actions performed by the user.
	 */
	public Trip generateRandomPath(double intensity, double fuzz, double speed) {
		List<Position> coordinates = new ArrayList<>();
		
		List<Action> actions = new ArrayList<>();
		
		// New date element for timestamp
		Date time = new Date();
		
		Coordinate previous = new Position(generateRandomCoordinateInsideRectangle(home), time);
		
		coordinates.add((Position) previous);
		
		// Shuffle the racks. This is used to create a random subset of zones to visit.
		List<Rack> shuffledRacks = new ArrayList<>(zones);
		
		Collections.shuffle(shuffledRacks);
		
		// Select a random amount of zones from the shuffled list of zones.
		for (Rack box : shuffledRacks.subList(0, ThreadLocalRandom.current().nextInt(0, shuffledRacks.size() + 1))) {
			// The target position.
			Coordinate target = new Position(generateRandomCoordinateInsideRectangle(box), new Date());
			
			for (Coordinate coordinate : getAllPointsAtSlopeBetweenCoordinates(previous, target, intensity, fuzz)) {
				time = addTime(time, Double.valueOf(intensity * speed).intValue());
				coordinates.add(new Position(coordinate, time));
			}
			
			previous = target;
			
			actions.add(generateNewAction(time, box));
		}
		
		Coordinate goal = generateRandomCoordinateInsideRectangle(home);
		
		for (Coordinate coordinate : getAllPointsAtSlopeBetweenCoordinates(previous, goal, intensity, fuzz)) {
			time = addTime(time, Double.valueOf(intensity * speed).intValue());
			coordinates.add(new Position(coordinate, time));
		}
		
		return new Trip(coordinates, actions);
	}
	
	/**
	 * Generates an action at 'time' where a product from the 'box' is either picked up or
	 * dropped. The product cannot be dropped if it has never been picked up.
	 * @param time Date object for when the action happened
	 * @param box A Rekt object where a random product is picked from
	 * @return Returns an action object with the product and time for whether the product was picked up or dropped.
	 */
	public Action generateNewAction(Date time, Rack box) {
		Product product = box.getRandomItem();
		int action = ThreadLocalRandom.current().nextInt(Action.DROP, Action.PICK_UP+1);
		
		if (!product.canBeDropped()) {
			action = 1;
		}
		
		return new Action(product, time, action);
	}
	
	/**
	 * A basic function to add time in seconds to a given Date time object
	 * @param time The date to add time to
	 * @param seconds Amount of seconds to add
	 * @return A Date object where the Date object 'time' has been added amount of 'seconds'.
	 */
	public Date addTime(Date time, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.SECOND, seconds);
		time = cal.getTime();
		return time;
	}

	public static void main(String[] args) throws JsonProcessingException {
		Rectangle home = new Rectangle(new Coordinate(0, 0), new Coordinate(10, 10));
	
		List<Rack> zones = new ArrayList<>();

		/**
		 * For each row in a rack, creates a new product and fills products with each product corresponding
		 * to amount of rows. Creates amount_of_racks racks.
		 * Adds every rack to zones.
		 */
		
		int amount_of_racks = 10;
		int amount_of_rows = 4;

		for (int i = 0; i < amount_of_racks; i++) {
			Collection<Product> products = new ArrayList<>();
			
			for (int j = 0; j < amount_of_rows; j++) {
				products.add(new Product());
			}
			Rack productRack = new Rack(new Coordinate(10*i, 20*i), new Coordinate(10*i+10, 20*i+10), products);
			zones.add(productRack);
		}
		
		DataMocker mocker = new DataMocker(home, zones);

		Trip trip = mocker.generateRandomPath(ThreadLocalRandom.current().nextInt(0, 500), 5, ThreadLocalRandom.current().nextInt(0, 60));		
		List<Position> path = trip.getPath();
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(trip);
		System.out.println(json);
	}
}