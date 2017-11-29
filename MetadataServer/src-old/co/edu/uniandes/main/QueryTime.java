package co.edu.uniandes.main;

/**
 * This class resets the name server. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.configuration.MetadataServerConfiguration;
import co.edu.uniandes.util.NamesUtil;

public class QueryTime {	
	private MetadataServerConfiguration configuration;
	private Process process;
	
	/**
	 * This is the constructor
	 */
	public QueryTime() {
		// reading the configuration properties
		configuration = new MetadataServerConfiguration ("nameServer.properties");
	}
	
	/**
	 * This method initiates the execution of the application
	 */
//	public void init() {
//		// reseting the name server
//		String r = NamesUtil.nameServerRingQueryTime(
//				configuration.getNameServerHostName(),
//				configuration.getNameServerPort());
//		
//		r = r.replace(';', '\n');
//		
//		System.out.println(r);
//	}
	
	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		QueryTime qt = new QueryTime();
//		qt.init();
	}
}
