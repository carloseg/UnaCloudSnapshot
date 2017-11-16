package co.edu.uniandes.tests;

/**
 * This class resets the name server. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.configuration.RTPTestManagerConfiguration;
import co.edu.uniandes.util.NamesUtil;

public class RingQueryTime {	
	private RTPTestManagerConfiguration configuration;
	private Process process;
	
	/**
	 * This is the constructor
	 */
	public RingQueryTime() {
		// reading the configuration properties
		configuration = new RTPTestManagerConfiguration ("nameServer.properties");
	}
	
	/**
	 * This method initiates the execution of the application
	 */
	public void init() {
		// reseting the name server
		String r = NamesUtil.nameServerRingQueryTime(
				configuration.getNameServerHostName(),
				configuration.getNameServerPort());
		
		r = r.replace(';', '\n');
		
		System.out.println(r);
	}
	
	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		RingQueryTime qt = new RingQueryTime();
		qt.init();
	}
}
