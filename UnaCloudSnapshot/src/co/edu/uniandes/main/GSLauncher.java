package co.edu.uniandes.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.globalstate.CoordinatorProcess;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

public class GSLauncher {
	private Configuration configuration;
	private CoordinatorProcess process;

	// El constructor carga las propiedades
	public GSLauncher() {
		configuration = new Configuration ("snapshot.properties");
	}

	
	public void init() {

		// carga los nombres de las VMs en un array
		String [] namesOfVM = getNamesOfVMs();
		
		CoordinatorProcess[] p = new CoordinatorProcess[namesOfVM.length];
		Thread[] tPool = new Thread[namesOfVM.length];
		
		CoordinatorProcess id0 = null;
		
		for(int i=0; i< namesOfVM.length;i++){
			
			// hay un puerto local por cada VM
			int localPort = configuration.getBase() + i;
			
			// para registrarse, cada thread envía la dirección IP del host y el puerto local
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
			
			p[i]  = new CoordinatorProcess (processId, configuration, namesOfVM[i]);

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
	
	public String [] getNamesOfVMs(){
		ArrayList<String> names = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("vmnames.txt"));
		    String line = br.readLine();
		    
		    while (line != null) {
		        names.add(line);
		        //System.out.println(line);
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

