package tdt4140.gr1864.app.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ActionTest {
	
	Action action;
	
	@Before
	public void setup() {
		action = new Action(1L, 1L, 2L);
	}
	
	@Test
	public void testGetTimeStampExpectOne() {
		Assert.assertEquals(1L, action.getTimeStamp());
	}
	
	@Test
	public void testGetActionTypeExpectOne() {
		Assert.assertEquals(1L, action.getActionType());
	}
	
	@Test
	public void testGetProductIdExpectedTwo() {
		Assert.assertEquals(2L, action.getProductID());
	}
}
