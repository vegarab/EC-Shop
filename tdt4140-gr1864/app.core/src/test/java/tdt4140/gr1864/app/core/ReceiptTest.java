package tdt4140.gr1864.app.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import tdt4140.gr1864.app.core.database.TestDataLoader;
import tdt4140.gr1864.app.core.database.DatabaseWiper;


public class ReceiptTest {
	
	private static Receipt receipt;
	
	private static ShoppingTrip shoppingTrip;

	@BeforeClass
	public static void setup() throws IOException {
		DatabaseWiper viper = new DatabaseWiper();
		viper.wipe();

		TestDataLoader loader = new TestDataLoader();
		shoppingTrip = loader.getTrip();
		receipt = new Receipt(shoppingTrip);
	}
	
	@Test
	public void testGetShoppingTrip() {
		Assert.assertEquals(shoppingTrip, receipt.getShoppingTrip());
	}
	
	@Test
	public void testGetInventory() {
		boolean allOne = true;

		for (Integer count : receipt.getInventory().values()) {
			if (count != 1) {
				allOne = false;
				break;
			}
		}
		
		Assert.assertFalse(allOne);
	}
	
	@Test
	public void testGetTotalPrice() {
		Assert.assertEquals(10.16, receipt.getTotalPrice(), 10e-5);
	}
}
