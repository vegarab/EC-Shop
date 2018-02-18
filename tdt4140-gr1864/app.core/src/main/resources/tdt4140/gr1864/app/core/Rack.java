package tdt4140.gr1864.app.core;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class Rack extends Rectangle {
	private Collection<Item> items;
	
	public Rack(Coordinate lower, Coordinate upper, Collection<Item> items) {
		super(lower, upper);
		
		this.items = items;
	}
	
	public Collection<Item> getItems() {
		return items;
	}
	
	public Item getRandomItem() {
		int index = 0;
		
		int target = ThreadLocalRandom.current().nextInt(items.size());
		
		for (Item item : items) {
			if  (index == target) {
				return item;
			}
			
			index += 1;
		}
		
		return null;
	}
}