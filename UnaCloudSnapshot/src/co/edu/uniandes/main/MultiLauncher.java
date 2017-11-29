package co.edu.uniandes.main;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import co.edu.uniandes.benchmark.Benchmarking;
import co.edu.uniandes.globalstate.CoordinatorProcess;


public class MultiLauncher {
	private Configuration configuration;

	public MultiLauncher() {
		configuration = new Configuration ("snapshot.properties");
	}

	public void init() {
		
		int numberOfProcesses = 100;
		
		String [] namesOfVM = getNamesOfVMs();
		
		CoordinatorProcess[] p = new CoordinatorProcess[numberOfProcesses];
		Thread[] t = new Thread[numberOfProcesses];
		
		for (int i=0; i<p.length; i++) {
			int localPort = configuration.getBase() + i;
			String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(),localPort,
					configuration.getNameServerHostName(),
					configuration.getNameServerPort());

			int processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);

			//Util.pause(1);
			if(i< namesOfVM.length){
				p[i] = new CoordinatorProcess(processId, configuration, namesOfVM[i]);
			}
			else{
				p[i] = new CoordinatorProcess(processId, configuration, "VM"+i);
			}
			
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
	public String [] getNamesOfVMs(){
		ArrayList<String> names = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("vmnames.txt"));
		    String line = br.readLine();
		    
		    while (line != null) {
		        names.add(line);
		        System.out.println(line);
		        line = br.readLine();
		    }
		    
		    br.close();
		} 
		catch(Exception e){}
		
		
		String [] namesVMs = new String[names.size()];
		for(int i=0; i< names.size();i++){
			namesVMs[i] = names.get(i);
		}
		return namesVMs;
	}
	
	public static void main(String[] args) {
		MultiLauncher ml = new MultiLauncher();
		ml.init();
	}

}

