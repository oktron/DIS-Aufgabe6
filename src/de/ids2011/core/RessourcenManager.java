package de.ids2011.core;

public interface RessourcenManager
{
	/**
	 * Writes data to page for the given transaction
	 * 
	 * @param taid
	 *            The transaction id
	 * @param pid
	 *            The page number
	 * @param data
	 *            Data
	 * @return true if write was successful, else false
	 */
	public boolean write(int taid, int pid, String data);

	/**
	 * Prepares the transaction to persist to disk
	 * 
	 * @param taid
	 *            The transaction id
	 * @return true if preparing was successful, else false
	 */
	public boolean prepare(int taid);

	/**
	 * Persists the transaction to disk
	 * 
	 * @param taid
	 *            The transaction id
	 * @return true if commit was successful, else false
	 */
	public boolean commit(int taid);

	/**
	 * Abort the transaction
	 * 
	 * @param taid
	 *            The transaction id
	 * @return true if rollback was successful, else false
	 */
	public boolean rollback(int taid);
}
