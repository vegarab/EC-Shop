package tdt4140.gr1864.app.core;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tdt4140.gr1864.app.core.storage.Shop;
import tdt4140.gr1864.app.core.storage.ShopDatabaseController;

public class ActionDatabaseControllerTest {
	
	ActionDatabaseController adc = new ActionDatabaseController();
	ShoppingTripDatabaseController stdc = new ShoppingTripDatabaseController();
	CustomerDatabaseController cdc = new CustomerDatabaseController();
	ShopDatabaseController sdc = new ShopDatabaseController();
	ProductDatabaseController pdc = new ProductDatabaseController();

	Action a1, a2;

	ShoppingTrip t1, t2;
	Customer c1;
	Shop s1;

	Product p1, p2;

	@BeforeClass
	public static void createDatabase() throws IOException {
		Path path = Paths.get("database.db");
		
		if (! Files.exists(path)) {
			CreateDatabase.main(null);
		}
	}
	
	@Before
	public void setup() {
		s1 = new Shop("Kings Road 2", 10);
		s1 = new Shop(s1.getAddress(),s1.getZip(), sdc.create(s1));

		c1 = new Customer("Ola", "Normann");
		c1 = new Customer(c1.getFirstName(), c1.getLastName(), cdc.create(c1));
		
		p1 = new Product("Produt", 1.0);
		p1 = new Product(pdc.create(p1), p1.getName(), p1.getPrice());
		
		t1 = new ShoppingTrip(c1, s1);
		t1 = new ShoppingTrip(stdc.create(t1), c1, s1);
		List<ShoppingTrip> trips = new ArrayList<ShoppingTrip>();
		trips.add(t1);
		c1.setShoppingTrips(trips);		

		a1 = new Action("1", 1, p1, t1);
	}

	@Test
	public void testCreateExpectPresistedObject() {
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				a1.getActionType(), 
				a1.getProduct(),
				stdc.retrieve(adc.create(a1))
			);

		Assert.assertEquals(t1.getShoppingTripID(), a1.getShoppingTrip().getShoppingTripID());
	}
	
	@Test
	public void testRetrieveExpecetPersistedObject() {
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				a1.getActionType(), 
				a1.getProduct(),
				stdc.retrieve(adc.create(a1))
			);
		a2 = adc.retrieve(a1.getShoppingTrip().getShoppingTripID(), a1.getTimeStamp());
		
		Assert.assertEquals(a1.getShoppingTrip().getShoppingTripID(), a2.getShoppingTrip().getShoppingTripID());
		Assert.assertEquals(a1.getTimeStamp(), a2.getTimeStamp());
	}
	
	@Test
	public void testDeleteExpectNull() {
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				a1.getActionType(), 
				a1.getProduct(),
				stdc.retrieve(adc.create(a1))
			);
		adc.delete(a1.getShoppingTrip().getShoppingTripID(), a1.getTimeStamp());
		
		Assert.assertEquals(null, adc.retrieve(a1.getShoppingTrip().getShoppingTripID(), a1.getTimeStamp()));
	}
	
	@Test
	public void testUpdateExpectNewlyPresistedObject() {
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				a1.getActionType(), 
				a1.getProduct(),
				stdc.retrieve(adc.create(a1))
			);
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				0, 
				a1.getProduct(),
				a1.getShoppingTrip()
			);
		adc.update(a1);
		a2 = adc.retrieve(a1.getShoppingTrip().getShoppingTripID(), a1.getTimeStamp());
		
		Assert.assertEquals(a1.getActionType(), a2.getActionType());
		Assert.assertEquals(a1.getTimeStamp(), a2.getTimeStamp());
		Assert.assertEquals(a1.getShoppingTrip().getShoppingTripID(), a2.getShoppingTrip().getShoppingTripID());
	}
	
	@Test
	public void testRetrieveOnShoppingTripIdExpectAllActionsForThatTrip() {
		a1 = new Action(
				Long.toString(a1.getTimeStamp()),
				a1.getActionType(), 
				a1.getProduct(),
				stdc.retrieve(adc.create(a1))
			);
		a2 = new Action("2", 1, p1, t1);
		a2 = new Action(
				Long.toString(a2.getTimeStamp()),
				a2.getActionType(),
				a2.getProduct(),
				stdc.retrieve(adc.create(a2))
			);
		List<Action> actions = adc.retrieve(t1.getShoppingTripID());
		
		Assert.assertEquals(actions.get(0).getTimeStamp(), a1.getTimeStamp());
		Assert.assertEquals(actions.get(1).getTimeStamp(), a2.getTimeStamp());
	}
	
	/*
	 * Deleting database after running test
	 */
	@AfterClass
	public static void finish() {
		Path path = Paths.get("database.db");
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	}

}
