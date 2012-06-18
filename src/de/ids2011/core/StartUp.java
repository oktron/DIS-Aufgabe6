package de.ids2011.core;


public class StartUp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Anwendung awd = new Anwendung("anwendung");
		Koordinator kdn = KoordinatorImpl.getInstance();
		int rscID = 1;
		
        // 1.Transaktion
		int taid = kdn.begin();
		RessourcenManager rm1 = new RessourcenManagerImpl(rscID);
        kdn.reg(rm1, taid);
		RessourcenManager rm2 = new RessourcenManagerImpl(++rscID);
        kdn.reg(rm2, taid);
		rm1.write(taid,taid*10, awd.getClientName());
		rm2.write(taid,taid*10, awd.getClientName());
		rm1.write(taid,taid*10+1, awd.getClientName());
		rm1.write(taid,taid*10+2, awd.getClientName());
		rm2.write(taid,taid*10+3, awd.getClientName());
		kdn.rollback(taid);
		
		// 2.Transaktion
		taid = kdn.begin();
		RessourcenManager rm3 = new RessourcenManagerImpl(++rscID);
        kdn.reg(rm3, taid);
		RessourcenManager rm4 = new RessourcenManagerImpl(++rscID);
        kdn.reg(rm4, taid);
		RessourcenManager rm5 = new RessourcenManagerImpl(++rscID);
        kdn.reg(rm5, taid);
		rm3.write(taid,taid*10, awd.getClientName());
		rm3.write(taid,taid*10, awd.getClientName());
		rm4.write(taid,taid*10+1, awd.getClientName());
		rm4.write(taid,taid*10+2, awd.getClientName());
		rm5.write(taid,taid*10+3, awd.getClientName());
		rm3.write(taid,taid*10+4, awd.getClientName());
		rm5.write(taid,taid*10+5, awd.getClientName());
		rm4.write(taid,taid*10+6, awd.getClientName());
		rm5.write(taid,taid*10+7, awd.getClientName());
		kdn.commit(taid);
	}

}
