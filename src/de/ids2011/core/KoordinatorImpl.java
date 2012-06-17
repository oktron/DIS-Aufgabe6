package de.ids2011.core;

import java.util.concurrent.atomic.AtomicInteger;

public class KoordinatorImpl implements Koordinator {
    private AtomicInteger counter = new AtomicInteger(1); 

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rollback(int taid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reg(RessourcenManager rm, int taid) {
		// TODO Auto-generated method stub
		return false;
	}

}
