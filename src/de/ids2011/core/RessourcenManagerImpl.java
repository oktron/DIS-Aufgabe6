package de.ids2011.core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RessourcenManagerImpl implements RessourcenManager {

	private File NUTZ_DATEN = null;
	private File LOG_DATEN = null;
	String toWrite;
	private Hashtable<String, List<String>> puffer = new  Hashtable<String, List<String>>();
    private AtomicInteger logSNCounter = new AtomicInteger(1); 

    public RessourcenManagerImpl(int resourceID){
    	String resourceName = resourceID + " nutzdaten";
    	String resourceLogName = resourceID + " logdaten";
        NUTZ_DATEN = new File(
        "./bestand/"+resourceName+".txt");
        LOG_DATEN = new File(
        "./bestand/"+resourceLogName+".txt");
    }
    	
	@Override
	public boolean write(int taid, int pid, String data) {
		boolean returnWert = false;
		List<String> pufferTADaten = new ArrayList<String>();
	    int logSN = this.getLogSN();
		String strLogSN = null;
		if (logSN<10) {
			strLogSN = "0"+logSN;
		}else strLogSN = logSN+"";
		toWrite = pid+","+strLogSN+","+ data;
			
		//Falls Transaktions ID exsistiert schon in der Puffer. 
		if (this.puffer.containsKey(taid+"")) {
			pufferTADaten = this.puffer.remove(taid+"");
			int commaPosition = toWrite.indexOf(",");
			//Falls in der taid Transaktion, gibt es auch gleich Pageid,
			//vorbereiten fuer ueberschreiben
			Boolean flag = false;
			for (int i = 0; i < pufferTADaten.size(); i++) {
				String altereVersion = pufferTADaten.get(i);
				String extrtPageid = altereVersion.substring(0, commaPosition);
				if (extrtPageid.equalsIgnoreCase(pid+"")) {
					int indexOfAltereVersion = pufferTADaten.indexOf(altereVersion);
					pufferTADaten.remove(altereVersion);
					pufferTADaten.add(indexOfAltereVersion, toWrite);
					flag = true;
				}
			}
			if (!flag) {
				pufferTADaten.add(toWrite);
			}
		}else {
			pufferTADaten.add(toWrite);
		}	
		this.puffer.put(taid+"", pufferTADaten);
		//pruefen ob Transaktion erfolgreich geschreiben wurdet
		if (this.puffer.containsValue(pufferTADaten)) returnWert = true;
		try {
			this.logDaten(pid,taid,logSN);
		} catch (Exception e) {
			e.printStackTrace();
		}				
		return returnWert;
	}

	@Override
	public boolean prepare(int taid) {
        boolean prepFlag = false;	
		if (!this.puffer.get(taid+"").isEmpty()) {
			prepFlag = true;
			int logSN = this.getLogSN();
			String strLogSN = null;
			if (logSN<10) {
				strLogSN = "0"+logSN;
			}else strLogSN = logSN+"";               
			String logFormat = strLogSN+","+taid+","+" vorbereitet"+";"+"\n";
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
		}else System.out.println("es gibt nicht dieser Transaktion ID");
		return prepFlag;
	}

	@Override
	public void commit(int taid) {
		try{			
			if (this.puffer.containsKey(taid+"")){
				List<String> TADaten = this.puffer.remove(taid+"");
				String einCommit = TADaten.get(0);
				int commaPosition = einCommit.indexOf(",");
				RandomAccessFile raf = new RandomAccessFile(NUTZ_DATEN, "rw");
				for (int i = 0; i < TADaten.size(); i++) {
				    String einzelneCommit = TADaten.get(i);
				    einzelneCommit = einzelneCommit +";" + "\n" ;
					long len = einzelneCommit.length() ;
					int pageID = Integer.valueOf(einzelneCommit.substring(0, commaPosition));
					raf.seek((pageID-1)*len);
					raf.writeBytes(einzelneCommit);
				}
				raf.close();
				
				int logSN = this.getLogSN();
				String strLogSN = null;
				if (logSN<10) {
					strLogSN = "0"+logSN;
				}else strLogSN = logSN+"";                
				String logFormat = strLogSN+","+taid+","+"   committed"+";"+"\n";
				long len = logFormat.length();
				RandomAccessFile rafLog = new RandomAccessFile(this.LOG_DATEN, "rw");;
				rafLog.seek(Integer.valueOf(logSN-1)*len);
				rafLog.writeBytes(logFormat); 
				rafLog.close();							
			}else System.out.println("Es gibt nicht dieser Transaktion ID ");
		}catch(IOException e){
			e.printStackTrace();
		}catch (Exception ex) {
            ex.printStackTrace();
		}
	}

	@Override
	public void rollback(int taid) {
		if (this.puffer.containsKey(taid+"")){ 
			this.puffer.remove(taid+"");
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
		}else System.out.println("Es gibt nicht dieser Transaktion ID");
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
	
	private void logDaten(int pageid, int taid, int logSN){
		String strLogSN = null;
		if (logSN<10) {
			strLogSN = "0"+logSN;
		}else strLogSN = logSN+"";
		int lastCommaPosition = this.toWrite.lastIndexOf(",");
		String dataSatz = this.toWrite.substring(lastCommaPosition+1);
		String logFormat = strLogSN +","+ taid +","+pageid+ ","+ dataSatz+";"+"\n";
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

}
