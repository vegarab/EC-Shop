package tdt4140.gr1864.app.core;

import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/* Uses test-data.json for testing */
public class DataLoaderTest {
	
	String pathToShoppingTrip;
	String pathToProducts;
	DataLoader loader;
	ProductDatabaseController pdc;
	
	@Before
	public void setup() {
		loader = new DataLoader();
		pdc = new ProductDatabaseController();
	}

	
	@Test
	public void testCoordinateLoadingFromFileExpectFirstCoordFromDataFile() {
		ShoppingTrip shoppingTrip = loader.getTrip();
		List<Coordinate> coords = shoppingTrip.getCoordinates();
		Coordinate coord = coords.get(0);
		double expectedX = 8.622905145346992;
		double expectedY = 4.569762307274866;
		long expectedTime = 1519216783919L;

		Assert.assertEquals(expectedX, coord.getX(), 0);
		Assert.assertEquals(expectedY, coord.getY(), 0);
		Assert.assertEquals(expectedTime, coord.getTimeStamp());
	}
	
	@Test
	public void testGroceryLoadingFromFileExcpectFirstGroceryFromDataFile() {
		List<Product> products = loader.getProducts();
		Product prod = products.get(0);
		String expectedName = "Pork - Back, Long Cut, Boneless";
		double expectedPrice = 0.55;

		Assert.assertEquals(expectedName, prod.getName());
		Assert.assertEquals(expectedPrice, prod.getPrice(), 0);
	}
	
	@Test
	public void testActionLoadingFromFileExpectFirstActionFromDataFile() {
		ShoppingTrip shoppingTrip  = loader.getTrip();
		List<Action> actions = shoppingTrip.getActions();
		Action action = actions.get(0);
		long expectedTime = 1519220923919L;
		int expectedType = 1;
		int expectedProduct = 52;
		
		Assert.assertEquals(expectedTime, action.getTimeStamp());
		Assert.assertEquals(expectedType, action.getActionType());
		Assert.assertEquals(expectedProduct, (int)action.getProduct().getID());
	}
}
