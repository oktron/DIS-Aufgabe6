package de.ids2011.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StartUp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int numberOfTransactions = getRandom(3, 5);
		Koordinator k = SingletonKoordinator.getInstance();
		
		for (int i = 0; i <= numberOfTransactions; i++) {
			int numberOfWrites = getRandom(2, 4);
			int taid = k.begin();
			
			for (int j = 0; j <= numberOfWrites; j++) {
				RessourcenManager rm = new FileRessourcenManager();
				k.reg(rm, taid);
				
				// data to write
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				
				rm.write(taid, taid*10+j, taid+" - "+dateFormat.format(date));
			}
			
			k.commit(taid);
		}
		
		int taid = k.begin();
		RessourcenManager rm1 = new FileRessourcenManager();
		RessourcenManager rm2 = new FileRessourcenManager();
		k.reg(rm1, taid);
		k.reg(rm2, taid);
		
		rm1.write(taid, 10, "abcdef");
		rm2.write(taid, 11, "123456");
		rm1.write(taid, 12, "0123456");
		k.rollback(taid);
		
		
		
//		Anwendung awd = new Anwendung("anwendung");
//		Koordinator kdn = KoordinatorImpl.getInstance();
//		int rscID = 1;
//		
//        // 1.Transaktion rollback
//		int taid = kdn.begin();
//		RessourcenManager rm1 = new RessourcenManagerImpl(rscID);
//        kdn.reg(rm1, taid);
//		RessourcenManager rm2 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm2, taid);
//		rm1.write(taid,taid*10, awd.getClientName());
//		rm2.write(taid,taid*10, awd.getClientName());
//		rm1.write(taid,taid*10+1, awd.getClientName());
//		rm1.write(taid,taid*10+2, awd.getClientName());
//		rm2.write(taid,taid*10+3, awd.getClientName());
//		kdn.rollback(taid);
//		
//		// 2.Transaktion commit
//		taid = kdn.begin();
//		RessourcenManager rm3 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm3, taid);
//		RessourcenManager rm4 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm4, taid);
//		RessourcenManager rm5 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm5, taid);
//		rm3.write(taid,taid*10, awd.getClientName());
//		rm3.write(taid,taid*10, awd.getClientName());
//		rm4.write(taid,taid*10+1, awd.getClientName());
//		rm4.write(taid,taid*10+2, awd.getClientName());
//		rm5.write(taid,taid*10+3, awd.getClientName());
//		rm3.write(taid,taid*10+4, awd.getClientName());
//		rm5.write(taid,taid*10+5, awd.getClientName());
//		rm4.write(taid,taid*10+6, awd.getClientName());
//		rm5.write(taid,taid*10+7, awd.getClientName());
//		kdn.commit(taid);
//		
//		//TODO Ressourcen-Manager sollen im Rahmen der Aufgabe auf 
//		//eine prepare-Anweisung gelegentlich mit aborted antworten, 
//		//um das Funktionieren des Protokolls auch im Fehlerfall zu zeigen.
//		
//		// 3.Transaktion commit
//		taid = kdn.begin();
//		RessourcenManager rm6 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm6, taid);
//		RessourcenManager rm7 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm7, taid);
//		RessourcenManager rm8 = new RessourcenManagerImpl(++rscID);
//        kdn.reg(rm8, taid);
//		rm6.write(taid,taid*10, awd.getClientName());
//		rm7.write(taid,taid*10, awd.getClientName());
//		rm8.write(taid,taid*10+1, awd.getClientName());
//		rm7.write(taid,taid*10+2, awd.getClientName());
//		rm6.write(taid,taid*10+3, awd.getClientName());
//		kdn.commit(taid);
//        
	}
	
	public static int getRandom(int min, int max)
	{
		return min + (int)(Math.random()*max);
	}

}
