package co.edu.uniandes.main;

/**
 * This class launches one process in the ring token passing application. 
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

public class NameServerReset {	
	private RingTokenPassingConfiguration configuration;
	private Process process;
	
	/**
	 * This is the constructor
	 */
	public NameServerReset() {
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

		// registering the process at the name server to obtain the processId
		String answer = NamesUtil.nameRingInsert(
				CommunicationsUtil.myIP(), 
				configuration.getNameServerHostName(), 
				configuration.getNameServerPort());
		
		int processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);
		
		// creating the process
		process = new Process (processId, configuration);
		
		// if the processId == 0, setting up the flag after a pause while the other processes are running
		if (processId == 0) {
			Util.pause(20);
			process.setFlag(true);
		}
		// starting the execution of the thread of the process
		process.run();
	}
	
	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		NameServerReset p = new NameServerReset();
		p.init();
	}
}
