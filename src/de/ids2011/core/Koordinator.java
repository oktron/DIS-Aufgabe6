package de.ids2011.core;

public interface Koordinator
{
	/**
	 * Begins a new transaction
	 * 
	 * @return int The transaction id
	 */
	public int begin();

	/**
	 * Commits the transaction
	 * 
	 * @param taid
	 *            The transaction id
	 * 
	 * @return true if commit was successful, else false
	 */
	public boolean commit(int taid);

	/**
	 * Rolls back the transaction
	 * 
	 * @param taid
	 *            The transaction id
	 * 
	 * @return true if rollback was successful, else false
	 */
	public boolean rollback(int taid);

	/**
	 * Registers the given resource manager with the coordinator for the given
	 * transaction
	 * 
	 * @param rm
	 *            The resource manager
	 * @param taid
	 *            The transaction id
	 * @return true if registration was successful, else false
	 */
	public boolean reg(RessourcenManager rm, int taid);

}
