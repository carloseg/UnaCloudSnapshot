package co.edu.uniandes.main;

/**
 * This class launches one process in the ring token passing application. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import co.edu.uniandes.configuration.RingTokenPassingConfiguration;
import co.edu.uniandes.process.Process;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

public class RTPLauncher {	
	private RingTokenPassingConfiguration configuration;
	private Process process;
	
	/**
	 * This is the constructor
	 */
	public RTPLauncher() {
		// reading the configuration properties
		configuration = new RingTokenPassingConfiguration ("rtp.properties");
	}
	
//	/**
//	 * This is the constructor
//	 */
//	public RTPLauncher(String path) {
//		// reading the configuration properties
//		configuration = new RingTokenPassingConfiguration (path);
//	}
	
	/**
	 * This method initiates the execution of the application
	 */
	public void init() {
		// registering the process at the name server to obtain the processId
		String answer = NamesUtil.nameInsert(
				CommunicationsUtil.myIP(), 
				configuration.getNameServerHostName(), 
				configuration.getNameServerPort());
		//System.out.println(answer);
		
		int processId = Integer.parseInt(answer.split(Constants.COLON)[1]);
		int maxToken = Integer.parseInt(answer.split(Constants.COLON)[2]);
		int maxBenchmark = Integer.parseInt(answer.split(Constants.COLON)[3]);
		
		System.out.println("My process Id is: " + processId);
		
		System.out.println("My IP address is  (ID:" + processId + "): " + CommunicationsUtil.myIP());

		// creating the process
		process = new Process (processId,maxToken,maxBenchmark, configuration);
		
		// if the processId == 0, setting up the flag after a pause while the other processes are running
		if (processId == 0) {
			Util.pause(40);
			process.setFlag(true);
		}

		// starting the execution of the thread of the process
		process.run();
	}
	
	/**
	 * This is the main method to launch the application.
	 * Usage: if properties file in not provided by CLI arguments, the default
	 * file properties will be rtp.properties
	 */
	public static void main(String[] args) {
		if ( args.length == 1) {
			System.out.println(args[0]);
		} else
		{
			System.out.println("Prueba Ring Token Passing");
		}
		RTPLauncher p = new RTPLauncher();
		p.init();
	}
}
