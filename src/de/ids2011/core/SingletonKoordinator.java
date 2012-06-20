package de.ids2011.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class SingletonKoordinator implements Koordinator
{
	/**
	 * Create thread-safe singleton. Described at
	 * http://www.javaworld.com/javaworld/jw-05-2003/jw-0530-letters.html?page=2
	 */
	private static final class SingletonHolder
	{
		static final Koordinator _instance = new SingletonKoordinator();
	}
	
	private AtomicInteger _currentTransactionId;
	private Map<Integer, Set<RessourcenManager>> _ressourcenManager;

	protected SingletonKoordinator()
	{
		_currentTransactionId = new AtomicInteger();
		_ressourcenManager = new HashMap<Integer, Set<RessourcenManager>>();
	}

	/**
	 * Get instance of SingletonKoordinator
	 * 
	 * @return The coordinator
	 */
	public static Koordinator getInstance()
	{
		return SingletonHolder._instance;
	}

	@Override
	public int begin()
	{
		int taid = _currentTransactionId.incrementAndGet();
		
		System.out.println("TA " + taid + " gestartet");
		
		return taid;
	}

	@Override
	public boolean commit(int taid) throws IllegalArgumentException
	{
		
		System.out.println("Commit für TA " + taid + " gestartet.");
		
		Set<RessourcenManager> rms;
		boolean allPrepared = true;
		boolean allCommited = true;
		
		if (!_ressourcenManager.containsKey(taid)) {
			throw new IllegalArgumentException("No transaction with this ID.");
		}
		
		rms = _ressourcenManager.get(taid);
		
		for (RessourcenManager rm : rms) {
			allPrepared = allPrepared && rm.prepare(taid);
		}
		
		if (allPrepared) {
			
			System.out.println("Alle Ressourcen Manager für TA " + taid + " prepared.");
			
			for (RessourcenManager rm : rms) {
				allCommited = allCommited && rm.commit(taid);
			}
		} else {
			
			System.err.println("TA" + taid + " konnte nicht prepared werden.");
			
			for (RessourcenManager rm : rms) {
				rm.rollback(taid);
			}
			allCommited = false;
		}
		
		// unregister resource manager
		if (allCommited) {
			removeManager(taid);
		}
		
		return allCommited;
	}

	@Override
	public boolean rollback(int taid) throws IllegalArgumentException
	{
		
		System.out.println("Rollback für TA" + taid + "gestartet.");
		
		boolean rollbackSuccessful = true;

		if (!_ressourcenManager.containsKey(taid)) {
			throw new IllegalArgumentException("No transaction with this ID.");
		}

		for (RessourcenManager rm : _ressourcenManager.get(taid)) {
			rollbackSuccessful = rollbackSuccessful && rm.rollback(taid);
		}
		
		// unregister resource manager
		if (rollbackSuccessful) {
			removeManager(taid);
		}

		return rollbackSuccessful;
	}

	@Override
	public boolean reg(RessourcenManager rm, int taid)
	{
		System.out.println("Ressourcen Manager für TA" + taid + " am Koordinator angemeldet.");
		
		boolean registered = false;
		Set<RessourcenManager> managerForTransaction;

		if (_ressourcenManager.containsKey(taid)) {
			managerForTransaction = _ressourcenManager.get(taid);
		} else {
			managerForTransaction = new HashSet<RessourcenManager>();
		}

		registered = managerForTransaction.add(rm);
		_ressourcenManager.put(taid, managerForTransaction);

		return registered;
	}

	private void removeManager(int taid) 
	{
		_ressourcenManager.remove(taid);
	}
}
