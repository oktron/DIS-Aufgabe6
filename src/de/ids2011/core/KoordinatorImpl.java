package de.ids2011.core;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KoordinatorImpl implements Koordinator {
    private AtomicInteger counter = new AtomicInteger(1);
	private static volatile Koordinator _instance;	
	private Hashtable<Integer, List<RessourcenManager>> rmList = new  Hashtable<Integer, List<RessourcenManager>>();
	private File LOG_DATEN = null;
    private AtomicInteger logSNCounter = new AtomicInteger(1); 

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
    
	private KoordinatorImpl(){
        LOG_DATEN = new File(
        "./bestand/"+"kdnLogdaten.txt");
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
			flag &= tmpRM.prepare(taid);
		}
		if (flag) {
			for (int i = 0; i < tmpList.size(); i++) {
				tmpRM = tmpList.get(i);
				tmpRM.commit(taid);
			}
		}
		int logSN = this.getLogSN();
		String strLogSN = null;
		if (logSN<10) {
			strLogSN = "0"+logSN;
		}else strLogSN = logSN+"";  
		String logFormat = null;
		logFormat = strLogSN+","+taid+","+"   committed"+";"+"\n";
		long len = logFormat.length();
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(this.LOG_DATEN, "rw");
			raf.seek(Integer.valueOf(logSN-1)*len);
			raf.writeBytes(logFormat); 
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Transaktion " + taid+ " commit: " + flag );
		return flag;
	}

	@Override
	public boolean rollback(int taid) {
		boolean flag = true;
		RessourcenManager tmpRM;
		List<RessourcenManager> tmpList = this.rmList.get(taid);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpRM = tmpList.get(i);
			flag = flag && tmpRM.rollback(taid);
		}
		if(flag){
			int logSN = this.getLogSN();
			String strLogSN = null;
			if (logSN<10) {
				strLogSN = "0"+logSN;
			}else strLogSN = logSN+"";               
			String logFormat = strLogSN+","+taid+","+"rueckgesetzt"+";"+"\n";
			long len = logFormat.length();
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(this.LOG_DATEN, "rw");
				raf.seek(Integer.valueOf(logSN-1)*len);
				raf.writeBytes(logFormat); 
				raf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Transaktion " + taid+ " rollback: " + flag );
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
		System.out.println("RessourcenManager von " + taid+" "+" rm"+ rm.getResourceID()  
				+" "+ " registriert sich: " + flag );
		return flag;
	}
	
	private int getLogSN(){
		int logSN;
        boolean flag;     
        do {     
        	logSN = this.logSNCounter.get();     
            flag = logSNCounter.compareAndSet(logSN, logSN + 1);     
        } while (!flag); 
		return logSN;
	}

}
