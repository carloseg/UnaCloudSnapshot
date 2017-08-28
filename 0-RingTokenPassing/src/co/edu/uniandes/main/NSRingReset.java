package co.edu.uniandes.main;

/**
 * This class resets the name server. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.process.Process;
import co.edu.uniandes.configuration.RingTokenPassingConfiguration;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

public class NSRingReset {	
	private RingTokenPassingConfiguration configuration;
	private Process process;
	
	/**
	 * This is the constructor
	 */
	public NSRingReset() {
		// reading the configuration properties
		configuration = new RingTokenPassingConfiguration ("rtp.properties");
	}
	
	/**
	 * This method initiates the execution of the application
	 */
	public void init() {
		// reseting the name server
		NamesUtil.nameServerRingReset(
				configuration.getNameServerHostName(),
				configuration.getNameServerPort());
	}
	
	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		NSRingReset nsr = new NSRingReset();
		nsr.init();
	}
}
