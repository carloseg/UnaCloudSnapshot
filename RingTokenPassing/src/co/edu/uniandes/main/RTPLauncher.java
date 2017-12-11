package co.edu.uniandes.main;

/**
 * This class launches one process in the ring token passing application. 
 *
* @author Carlos Eduardo Gómez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
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
		System.out.println("Instance RTP running...");
		configuration = new RingTokenPassingConfiguration ("rtp.properties");
	}
	
	
	/**
	 * This method initiates the execution of the application
	 * 1 process per execution
	 */
	public void init() throws Exception {
		// registering the process at the name server to obtain the processId
		String answer = NamesUtil.nameInsert(
				CommunicationsUtil.myIP(), 
				configuration.getNameServerHostName(), 
				configuration.getNameServerPort());
		if(answer.equals(Constants.RING_IN_PROGRESS)){
			throw new Exception("This instance couldn't join the ring because there is already a ring in a current execution");
		}
		int processId = Integer.parseInt(answer.split(Constants.COLON)[1]);
		int maxToken = Integer.parseInt(answer.split(Constants.COLON)[2]);
		int maxBenchmark = Integer.parseInt(answer.split(Constants.COLON)[3]);
		int pauseToStartRing = Integer.parseInt(answer.split(Constants.COLON)[4]);
		int pauseBenchmark = Integer.parseInt(answer.split(Constants.COLON)[5]);
		int basePort = Integer.parseInt(answer.split(Constants.COLON)[6]);
		
		System.out.println("My process Id is: " + processId);
		
		System.out.println("My IP address is  (ID:" + processId + "): " + CommunicationsUtil.myIP());

		// creating the process
		process = new Process (processId,maxToken,maxBenchmark,pauseBenchmark,basePort, configuration);
		
		// if the processId == 0, setting up the flag after a pause while the other processes are running
		if (processId == 0) {
			Util.pause(pauseToStartRing);
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
			System.out.println("Ring Token Passing Started");
		}
		RTPLauncher p = new RTPLauncher();
		try {
			p.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
