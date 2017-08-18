package co.edu.uniandes.main;

/**
 * This class launches one process in the ring token passing application. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.util.CommunicationsUtil;

public class PruebaMyIp {	
	/**
	 * This is the constructor
	 */
	public PruebaMyIp() {
	}
	
	/**
	 * This is the constructor
	 */
	public PruebaMyIp(String path) {
	}
	
	/**
	 * This method initiates the execution of the application
	 */
	public void init() {
		System.out.println(CommunicationsUtil.myIP());
	}
	
	/**
	 * This is the main method to launch the application.
	 * Usage: if properties file in not provided by CLI arguments, the default
	 * file properties will be rtp.properties
	 */
	public static void main(String[] args) {
		PruebaMyIp p = new PruebaMyIp();
		p.init();
	}
}
