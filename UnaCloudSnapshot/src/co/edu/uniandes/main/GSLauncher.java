package co.edu.uniandes.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.globalstate.CoordinatorProcess;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;

/**
 * This class is the one that initialize the processes.
 * One process per VM in the physical host.
 * 
* @author Carlos Eduardo Gómez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/

public class GSLauncher {
	private Configuration configuration;
	private CoordinatorProcess process;

	public GSLauncher() {
		configuration = new Configuration ("us.properties");
	}

/**
 * This method loads the configuration and creates the processes from the names of the VMs in the host.	
 */
	public void init() {

		String [] namesOfVM = getNamesOfVMs();
		
		CoordinatorProcess[] p = new CoordinatorProcess[namesOfVM.length];
		Thread[] tPool = new Thread[namesOfVM.length];
		
		CoordinatorProcess id0 = null;
		
		for(int i=0; i< namesOfVM.length;i++){
			
			// One local port per  VM
			int localPort = configuration.getBase() + i;
			
			String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(), localPort, 
					configuration.getNameServerHostName(),
					configuration.getNameServerPort());

			// formato de la respuesta
			// answer = "OK. ProcessId = " + processId + " name - " + address + " - " + localPort;
			// [0] = "OK."
			// [1] = "ProcessId"
			// [2] = "="
			// [3] = processId
			// ...
			
			int pause = configuration.getPause();
			int processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);
			
			p[i]  = new CoordinatorProcess (processId, localPort, configuration, namesOfVM[i]);

			Thread t = new Thread(p[i]);
			tPool[i] = t;
			
			
			if ( processId == 0) {
				id0= p[i];
			}
		}
		for (int i=0; i<p.length; i++) {
			tPool[i].start();
		}
		if(id0 != null){
			id0.getGlobalState();
		}
		
	}
	/**
	 * This method searches in the file vmnames.txt the names of the VM in the host.
	 * @return Array of Strings. Each position in the array contains a name of one VM.
	 */
	public String [] getNamesOfVMs(){
		ArrayList<String> names = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("vmnames.txt"));
		    String line = br.readLine();
		    
		    while (line != null) {
		        names.add(line);
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
		GSLauncher p = new GSLauncher();
		p.init();
	}
}

