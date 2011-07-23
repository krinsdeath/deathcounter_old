package net.krinsoft.deathcounter.interfaces;

public interface IDatabase {
	
	/**
	 * specifies a method to save the user to disk
	 * 
	 */
	public void save();
	
	/**
	 * specifies a method to load the user into memory from disk
	 * 
	 */
	public void load();
	
	/**
	 * specifies a method to update portions of the user's database entry
	 * 
	 */
	public void update(String entry);
	
	/**
	 * specifies a method to insert new users into the database
	 * 
	 */
	public void insert();
}
