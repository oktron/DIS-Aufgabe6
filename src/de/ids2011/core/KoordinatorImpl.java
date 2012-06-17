package de.ids2011.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KoordinatorImpl implements Koordinator {
    private AtomicInteger counter = new AtomicInteger(1);
	private static volatile Koordinator _instance;	
	private Hashtable<Integer, List<RessourcenManager>> rmList = new  Hashtable<Integer, List<RessourcenManager>>();

	public static Koordinator getInstance() { 
		if (_instance == null) {
			synchronized (Koordinator.class) {
				if (_instance == null) {
					_instance = new KoordinatorImpl();
				}
			}
		}
		return _instance;
	}
    
	
	@Override
	public int begin() {
		int taid;
        boolean flag;     
        do {     
            taid = counter.get();       
            flag = counter.compareAndSet(taid, taid + 1);     
        } while (!flag);  
        return taid;
	}

	@Override
	public boolean commit(int taid) {
		boolean flag = true;
		RessourcenManager tmpRM;
		List<RessourcenManager> tmpList = this.rmList.get(taid);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpRM = tmpList.get(i);
			flag = flag && tmpRM.prepare(taid);
		}
		if (flag) {
			for (int i = 0; i < tmpList.size(); i++) {
				tmpRM = tmpList.get(i);
				tmpRM.commit(taid);
			}
		}
		return flag;
	}

	@Override
	public boolean rollback(int taid) {
		boolean flag = false;
		RessourcenManager tmpRM;
		List<RessourcenManager> tmpList = this.rmList.get(taid);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpRM = tmpList.get(i);
			tmpRM.rollback(taid);
		}
		if(this.rmList.get(taid) == null) flag = true;
		return flag;
	}

	@Override
	public boolean reg(RessourcenManager rm, int taid) {
		boolean flag = false;
		List<RessourcenManager> tmpList = new ArrayList<RessourcenManager>();
		if (this.rmList.containsKey(taid)){ 
			tmpList = this.rmList.remove(taid);
			if(!tmpList.contains(rm)) tmpList.add(rm);
			else {
				System.out.println("Der RessourcenManager ist schon enthalten");
				return flag;
			}
		}
		else tmpList.add(rm);
		this.rmList.put(taid, tmpList);
		flag = true;
		return flag;
	}

}
