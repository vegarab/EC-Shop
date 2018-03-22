package tdt4140.gr1864.app.core;

import java.util.List;
import java.util.Observable;

import tdt4140.gr1864.app.core.interfaces.UserInterface;

public class Customer extends Observable implements UserInterface {

	private int customerId;

	private String firstName;
	private String lastName;
	/* has a default value for Customers without demographic data */
	private String address = null;
	private int zip = 0;

	private List<ShoppingTrip> shoppingTrips;

	private boolean hasUpdated;
	
	/**
	 * @param customerId		id provided by database
	 * @param firstName			name of customer
	 * @param lastName			name of customer
	 * @param shoppingTrips 	trips of customer
	 */
	public Customer(int customerId, String firstName, String lastName, 
			List<ShoppingTrip> shoppingTrips) {
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.shoppingTrips = shoppingTrips;
		this.hasUpdated = false;
	}
	
	/**
	 * Constructor used by CustomerDatabaseController when there is an address
	 * @param customerId		id provided by database
	 * @param firstName			name of customer
	 * @param lastName			name of customer
	 */
	public Customer(String firstName, String lastName, int customerId,
			 String address, int zip) {
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.zip = zip;
	}

	/**
	 * @param firstName			name of customer
	 * @param lastName			name of customer
	 * @param customerId		id provided by database
	 */
	public Customer(String firstName, String lastName, int customerId) { 
		this.firstName = firstName;
		this.lastName = lastName;
		this.customerId = customerId;
	}
	
	/**
	 * @param firstName		name of customer
	 * @param lastName		name of customer
	 */
	public Customer(String firstName, String lastName) { 
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public int getUserId() {
		return customerId;
	}

	public void setUserId(int userId) {
		this.customerId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<ShoppingTrip> getShoppingTrips() {
		return shoppingTrips;
	}

	public void setShoppingTrips(List<ShoppingTrip> shoppingTrips) {
		this.shoppingTrips = shoppingTrips;
	}
	
	public int getZip() {
		return zip;
	}

	/**
	 * Sets new zip code and notifies observer
	 * @param zip new zip code
	 */
	public void setZip(int zip) {
        setChanged();
		notifyObservers();
		clearChanged();
		this.zip = zip;
	}
	
	public String getAddress() {
		return address;
	}

	/**
	 * Sets new address and notifies observer
	 * @param address new address
	 */
	public void setAddress(String address) {
		setChanged();
		notifyObservers();
		clearChanged();
		this.address = address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + customerId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} 
		else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} 
		else if (!lastName.equals(other.lastName))
			return false;
		if (customerId != other.customerId)
			return false;
		return true;
	}
}

