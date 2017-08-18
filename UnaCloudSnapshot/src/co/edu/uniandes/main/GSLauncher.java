package co.edu.uniandes.main;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.globalstate.CoordinatorProcess;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

public class GSLauncher {
	private Configuration configuration;
	private CoordinatorProcess process;

	public GSLauncher() {
		configuration = new Configuration ("snapshot.properties");
	}

	public void init() {
//		String answer = NamesUtil.nameInsert(configuration.getMyIP(),
		String answer = NamesUtil.nameInsert(CommunicationsUtil.myIP(),
				configuration.getNameServerHostName(),
				configuration.getNameServerPort());

		int pause = configuration.getPause();
		int processId = Integer.parseInt(answer.split(Constants.SPACE)[3]);

//		long initTime = System.nanoTime();
//		long endTime = System.nanoTime();
//		
//		long elapsedTime = endTime-initTime;
//		
//		System.out.println("Elapsed Time: " + elapsedTime);
//		System.out.println("Tiempo de encendido de la VM - Elapsed Time: " + elapsedTime/1000000000.0 + " s");
		
		process = new CoordinatorProcess (processId, configuration);

		Thread t = new Thread(process);
		t.start();
		
		if ( processId == 0) {
			Util.pause(pause);
			process.getGlobalState();
		}
	}
	
	public static void main(String[] args) {
		GSLauncher p = new GSLauncher();
		p.init();
	}
}

/*
 * Los cambios para duplicar el proceso: 
 * 1) Cambiar el id del proceso
 * 2) Eliminar la pausa
 * 3) Eliminar el llamado a obtener el estado global
 */
