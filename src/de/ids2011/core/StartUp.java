package de.ids2011.core;


public class StartUp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Anwendung awd = new Anwendung("anwendung");
		Koordinator kdn = new KoordinatorImpl();
		int taid = kdn.begin();
		RessourcenManager rm1 = new RessourcenManagerImpl(taid);
		RessourcenManager rm2 = new RessourcenManagerImpl(taid);

		rm1.write(taid,taid*10, awd.getClientName());
		rm2.write(taid,taid*10, awd.getClientName());
		rm1.write(taid,taid*10+1, awd.getClientName());
		rm1.write(taid,taid*10+2, awd.getClientName());
		rm2.write(taid,taid*10+3, awd.getClientName());
		
		kdn.commit(taid);

	}

}
