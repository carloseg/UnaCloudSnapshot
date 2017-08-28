package co.edu.uniandes.main;

/**
 * This class launches the ring token passing application. In this case multiple
 * processes are launched automatically.
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

public class RingTokenPassingMultiLauncher {
	private RingTokenPassingConfiguration configuration;

	/**
	 * This is the constructor
	 */
	public RingTokenPassingMultiLauncher() {
		// reading the configuration properties
		configuration = new RingTokenPassingConfiguration("rtp.properties");
	}

	/**
	 * This method initiates the execution of the application
	 */
	public void init() {
		// reseting the name server
//		NamesUtil.nameServerRingReset(
//				configuration.getNameServerHostName(),
//				configuration.getNameServerPort());
		
		int numberOfProcesses = 8;
		
		int processId = -1;

		Process[] p = new Process[numberOfProcesses];
		Thread[] t = new Thread[numberOfProcesses];
		
		for (int i = 0; i < p.length; i++) {
			// registering the process at the name server to obtain the processId
			String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(),
					configuration.getNameServerHostName(),
					configuration.getNameServerPort());

			processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);

			// creating the processes and the threads
			p[i] = new Process(processId, configuration);
			t[i] = new Thread(p[i]);
		}

		// the thread associated to the processId == 0 is not launched
		for (int i = 1; i < p.length; i++) {
			t[i].start();
		}

		// if the processId == 0, it needs a pause while the other processes are running
		if (processId==0) {
			Util.pause(20);
		}
		// setting up the flag
		p[0].setFlag(true);
		
		// the thread associated to the process with processId == 0 is launched
		t[0].start();
	}

	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		System.out.println("Prueba 2");
		RingTokenPassingMultiLauncher p = new RingTokenPassingMultiLauncher();
		p.init();
	}
}
