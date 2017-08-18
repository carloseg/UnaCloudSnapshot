package co.edu.uniandes.main;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.benchmark.Benchmarking;
import co.edu.uniandes.globalstate.CoordinatorProcess;


public class MultiLauncher {
	private Configuration configuration;

	public MultiLauncher() {
		configuration = new Configuration ("snapshot.properties");
	}

	public void init() {
		
		int numberOfProcesses = 100;
		
		CoordinatorProcess[] p = new CoordinatorProcess[numberOfProcesses];
		Thread[] t = new Thread[numberOfProcesses];
		
		for (int i=0; i<p.length; i++) {

			String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(),
					configuration.getNameServerHostName(),
					configuration.getNameServerPort());

			int processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);

			//Util.pause(1);

			p[i] = new CoordinatorProcess(processId, configuration);
			t[i] = new Thread(p[i]);
		}

		// no se lanza el servidor 0
		for (int i=1; i<p.length; i++) {
			t[i].start();
		}
//		Util.pause(2);
		t[0].start();
		p[0].getGlobalState();
		
//        System.out.println("Benchmarking process - After global snapshot");
//        long initTime = System.nanoTime();
//		Benchmarking.primeCalculatorIntegers(2000);
//        long endTime = System.nanoTime();
//        System.out.println("End of the benchmarking process");
//
//		long elapsedTime = endTime-initTime;
//		System.out.println("Snapshot time: " + elapsedTime/1000000000.0 + " s");

	}
	
	public static void main(String[] args) {
		MultiLauncher ml = new MultiLauncher();
		ml.init();
	}

}

