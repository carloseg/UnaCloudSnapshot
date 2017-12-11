package co.edu.uniandes.main;

/**
 * This class launches the ring token passing application. In this case multiple
 * processes are launched automatically.
 *
* @author Carlos Eduardo Gómez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
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
	 * This method initiates the execution of the application.
	 * Initiates 8 processes of RTP
	 */
	public void init() throws Exception {

		int numberOfProcesses = 8;

		int processId = -1;

		Process[] p = new Process[numberOfProcesses];
		Thread[] t = new Thread[numberOfProcesses];
		int pauseToStartRing =0;
		boolean the0isHere = false;
		for (int i = 0; i < p.length; i++) {
			// registering the process at the name server to obtain the processId
			String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(),
					configuration.getNameServerHostName(),
					configuration.getNameServerPort());

			if(answer.equals(Constants.RING_IN_PROGRESS)){
				throw new Exception("This instance couldn't join the ring because there is already a ring in a current execution");
			}
			processId = Integer.parseInt(answer.split(Constants.COLON)[1]);
			if(processId == 0){
				the0isHere = true;
			}
			int maxToken = Integer.parseInt(answer.split(Constants.COLON)[2]);
			int maxBenchmark = Integer.parseInt(answer.split(Constants.COLON)[3]);
			pauseToStartRing = Integer.parseInt(answer.split(Constants.COLON)[4]);
			int pauseBenchmark = Integer.parseInt(answer.split(Constants.COLON)[5]);
			int basePort = Integer.parseInt(answer.split(Constants.COLON)[6]);

			// creating the processes and the threads
			p[i] = new Process(processId,maxToken,maxBenchmark,pauseBenchmark,basePort, configuration);
			t[i] = new Thread(p[i]);
		}

		if(the0isHere){
			// the thread associated to the processId == 0 is not launched
			for (int i = 1; i < p.length; i++) {
				t[i].start();
			}
			//esto va a hacer la pausa del proceso 0
			Util.pause(pauseToStartRing);
			// setting up the flag
			p[0].setFlag(true);

			// the thread associated to the process with processId == 0 is launched
			t[0].start();
		}
		else{
			for (int i = 0; i < p.length; i++) {
				t[i].start();
			}
		}


	}

	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		System.out.println("Prueba 2");
		RingTokenPassingMultiLauncher p = new RingTokenPassingMultiLauncher();
		try {
			p.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
