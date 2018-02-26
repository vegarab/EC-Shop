package tdt4140.gr1864.app.core;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

public class ShoppingTripTest {
	
	ShoppingTrip shoppingTrip;
	
	@Before
	public void setup() {
		DataLoader loader = new DataLoader();
		String path = "../../src/main/resources/test-data.json";
		shoppingTrip = loader.loadShoppingTrips(path);
	}

	@Test
	public void testGetStartExpectFirstTimeFromTestDataFile() {
		long expectedTime = 1519216783919L;
		Assert.assertEquals(expectedTime, shoppingTrip.getStart());
	}
	
	@Test
	public void testGetEndExpectedLastTimeFromTestDataFile() {
		long expectedTime = 1519239823919L;
		Assert.assertEquals(expectedTime, shoppingTrip.getEnd());
	}
	
	@Test
	public void testCoordinateListContainsCorrectCoordinatesExpectedSameAsTestDataFile() {
		List<Coordinate> coords = shoppingTrip.getCoordinates();
		Coordinate coord = coords.get(1);
		double expectedX = 8.56571577118933;
		double expectedY = 3.9484455129872824;
		long expectedTime = 1519216963919L;

		Assert.assertEquals(expectedX, coord.getX(), 0);
		Assert.assertEquals(expectedY, coord.getY(), 0);
		Assert.assertEquals(expectedTime, coord.getTimeStamp());
	}

	@Test
	public void testActionListContainsCorrectActionsExpectedSameAsTestDataFile() {
		List<Action> actions = shoppingTrip.getActions();
		Action action = actions.get(1);
		long expectedTime = 1519222543919L;
		long expectedType = 1L;
		long expectedProduct = 32L;
		
		Assert.assertEquals(expectedTime, action.getTimeStamp());
		Assert.assertEquals(expectedType, action.getActionType());
		Assert.assertEquals(expectedProduct, action.getProductID());
	}
}