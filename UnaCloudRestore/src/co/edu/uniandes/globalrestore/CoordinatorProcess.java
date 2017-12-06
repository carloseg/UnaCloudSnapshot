package co.edu.uniandes.globalrestore;

/*
 * CoordinatorProcess.java
 * 
 * This class implements the global restoration algorithm.
 * 
 * This class corresponds a pure peer-to-peer system that depends of a name server to obtain
 * the processID and the port number of the other participants in the system.
 * 
 * This class has two behaviors: as a server and as a client.
 * 
 * Any peer can initiate the execution. The first peer to register with the name server 
 * initiates the execution of the algorithm.
 * 
 * This version supports one VM per peer, so if several VMs are running on a host, it
 * must be run the same amount of CoordinatorProcess instances in that host.
 * 
 * Each CoordinatorProcess initiates launching their constructor method. After that, they
 * launches the run() method, which blocks until the stater peer initiates the execution of
 * the algorithm.
 * 
 * The starter peer launches the method getGlobalState(), which execute the steps of the
 * algorithm. Then, the other processes behave as servers in the run () method.
 *
 * @author Carlos Eduardo Gomez Montoya
 * @since 2017
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.LoggerUtil;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;
import co.edu.uniandes.vm.VM;

public class CoordinatorProcess implements Runnable {
	// These two values are assigned from the launcher. If processId == 0, 
	// this process is the initiator.
	private int processId;
	private Configuration configuration;
	
	private String vmname;

	private boolean[] done;
	private int systemSize;

	private ServerSocket listener;
	private BufferedReader reader;

	private int localPort;
	
	private long initTime;
	private long endTime;
	private long ta;
	private long tb;
	private long tc;
	
	private Logger serverLog;
	private VM vm;
	
	private String timesOfGS;
	
	
	private String metadataServer;

	/**
	 * This is the constructor.
	 * @param localPort2 
	 * @param id It is the id of the process.
	 * @param configuration It is the general configuration of the application.
	 * It includes the values contained in the properties file.
	 */
	public CoordinatorProcess(int processId, int localPort2, Configuration configuration,String vmName) {
		this.configuration = configuration;
		this.processId = processId;
		
		this.vm = new VM(configuration);
		
		this.vmname = vmName;
		
		timesOfGS = "";
				
//		vmname = configuration.getVmname();
		
		done = new boolean[configuration.getMax()];

		loggerSetUp(processId);
		serverLog.info("Process " + processId + " in charge of VM: "+vmname+" is running ...");
		
		// el puerto lo recibe del Launcher
		localPort = localPort2;
		
		metadataServer = configuration.getNameServerHostName();
	}


	/**
	 * This method starts the process server mode. It listens to a port waiting for
	 * messages to implement the global restore algorithm.
	 */
	public void run() {
		try {
			String s = "";
			
			// The process is blocked waiting for another peer in the client mode.
			listener = new ServerSocket(localPort);

			String starter = "";
			while (true) {

				// The process is blocked waiting for a message from a peer
				Socket socket = listener.accept();

				// Ver si se puede hacer una sola vez. EVALUAR
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String line = reader.readLine();
				serverLog.info("Received: " + line);

				// A read message can be:
				// RESTORE + SPACE + id or    
				// RESTORE_DONE + SPACE + id
				
				// Sender identification no matter what kind of message has been received.
				int sender = Integer.parseInt(line.split(Constants.SPACE)[1]);

				// If it is the RESTORE message (it does not apply for the starter):
				if (line.startsWith(Constants.RESTORE) == true) {
					
//					String answer = NamesUtil.nameInitialTime(
//							processId,
//							configuration.getNameServerHostName(), 
//							configuration.getNameServerPort(), 1);
					
					// ***** modificar para que retorne el tiempo y enviar el tiempo local
					// con el mensaje RESTORE_DONE
					double t = step1();
					
					// Registrar el tiempo de terminaciÃ³n del restore
//					answer = NamesUtil.nameEndTime(
//							processId, 
//							configuration.getNameServerHostName(), 
//							configuration.getNameServerPort(), 1);
					
					// It sends a DONE message to the starter process
					String done = Constants.RESTORE_DONE + Constants.SPACE + processId
							+ Constants.SPACE + "VM Name:"+ vmname + Constants.SPACE + t;
					
					// It queries the starter process network information.
					starter = NamesUtil.nameQuery(
							configuration.getProcessHostNamePrefix() + sender, 
							processId, 
							configuration.getNameServerHostName(),
							configuration.getNameServerPort());		
					
					// It sends the DONE message to the starter process.
					send(starter, done);	
					Util.pause(5);
					System.exit(0);

//					step2();
				} else {
					// If the message is a DONE. 
					// This is only applicable for the starter process
					if (line.startsWith(Constants.RESTORE_DONE)) {
						// It marks the sender process of the message
						markDone(sender);
						String [] values = line.split(" "); 
						timesOfGS += values[3].split(":")[1]+":"+values[4]+";";
						
						// If all peers are marked, it execute the step 2.
						if (numberOfDone() == systemSize) {
//							step2();
							serverLog.info("The global restore has finished.");

							endTime = System.nanoTime();
							long elapsedTime = endTime-initTime;
//							System.out.println("Global Restore time: " + elapsedTime/1000000000.0 + " s");
							
							double elapsedTimeInSeconds=elapsedTime/1000000000.0 ;
							serverLog.info("Global Restore time: " + elapsedTimeInSeconds+ " s");
							
							timesOfGS+= elapsedTimeInSeconds;
							
							System.out.println("Sending times: "+timesOfGS);
							
							notifyEnd();
							
//							elapsedTime = ta-initTime;
//							System.out.println("Antes del Local Restore time: " + elapsedTime/1000000000.0 + " s");
							
							
//							elapsedTime = tc-tb;
//							System.out.println("Después del Local Restore time: " + elapsedTime/1000000000.0 + " s");
//							
//							elapsedTime = endTime-tc;
//							System.out.println("Tiempo de espera time: " + elapsedTime/1000000000.0 + " s");
							
							
							Util.pause(5);
							System.exit(0);
						}
					} else {
					} 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method broadcast a message to other processes in the system.
	 * The list of the processes is obtained from the name server.
	 * 
	 * @param message The message to send.
	 */
	public void broadcast(String message) {
		String list = NamesUtil.nameQueryList(processId, metadataServer,
				configuration.getNameServerPort());

		if (list.equals("") == false) {
			String[] server = list.split(Constants.SEMICOLON);

			for (int i = 0; i < server.length; i++) {
				send(server[i], message);
			}
		}
	}
	
	/**
	 * This method notify the data to the meta data server: global time. small times.
	 * 
	 */
	public void notifyEnd() {
		NamesUtil.nameQueryEnding(timesOfGS, metadataServer,
				configuration.getNameServerPort());

	}

	/**
	 * This method send a message to a process.
	 * 
	 * @param destination The destination process of the message.
	 * 
	 * @param message The message to broadcast.
	 *            
	 * @return boolean The success of the communication.
	 */
	private boolean send(String destination, String message) {
		// The destination process comes in this format:
		// processName:address:port
		// It splits the string in a String array
		String[] aServer = destination.split(Constants.COLON);

		// Element [0] --> id
		// Element [1] --> address (or hostname)
		// Element [2] --> port
		String remoteName = aServer[1];
		int remotePort = Integer.parseInt(aServer[2]);

		serverLog.info("Sent: " + message + " to: " + destination);
		boolean b = CommunicationsUtil.sendMessage(message, remoteName, remotePort);

		return b;
	}
	
	/**
	 * This method marks a process indicating that its local restore is done.
	 * 
	 * @param index The process id.
	 */
	public void markDone(int index) {
		done[index] = true;
	}

	/**
	 * This method reports if the specified process has taken its local restore or not.
	 * 
	 * @param index The process id.
	 *            
	 * @return boolean The state of the process.
	 */
	public boolean isDone(int index) {
		return done[index];
	}

	/**
	 * This method reports the number of processes that have taken their local restore processes.
	 * 
	 * @return int The number of DONE messages received.
	 */
	public int numberOfDone() {
		int number = 0;
		for (int i = 0; i < done.length; i++) {
			if (isDone(i)) {
				number++;
			}
		}
		return number;
	}

	/**
	 * This method launches the global state procedure.
	 */
	public void getGlobalState() {
		String s = "";

		
		
		
		// It queries the number of processes in the system
		systemSize = Integer.parseInt(
				NamesUtil.nameQuerySize(
						processId, 
						configuration.getNameServerHostName(), 
						configuration.getNameServerPort()));
		
		String restore = Constants.RESTORE + Constants.SPACE + processId;
		

		//Before start broadcasting, the process must wait for the authorization to start
		String confirmation =  NamesUtil.notifyProcessOisReady(CommunicationsUtil.myIP(),
				configuration.getNameServerHostName(),
				configuration.getNameServerPort());
		
		if(confirmation.equals("START")){
			initTime = System.nanoTime();
			serverLog.info("Starting the global restore");
			serverLog.info("Broadcasting " + restore);
			broadcast(restore);
			
			step1();
		}
		
	}

	private double step1() {
		String s;
		// 3. take the local restore
		serverLog.info("Starting the restoration of "+ vmname);

//		String answer = NamesUtil.nameInitialTime(
//				processId,
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);
		
		ta = System.nanoTime();
		s = vm.resume(vmname);

		
		tb = System.nanoTime();
		
		long elapsedTime = tb-ta;
		
//		System.out.println("Local Restore time: " + elapsedTime/1000000000.0 + " s");
		serverLog.info("Restoration time: " + elapsedTime/1000000000.0 + " s");
		//		answer = NamesUtil.nameEndTime(
//				processId, 
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);

		serverLog.info("Ending the restoration of "+ vmname);
		
		//if it is the process 0 it will add to the times his time
		double timeOfThisProcess = elapsedTime/1000000000.0;
		timesOfGS += vmname+":"+timeOfThisProcess+";";
		markDone(0);
		if (numberOfDone() == systemSize) {
			serverLog.info("The restoration has finished.");
			endTime = System.nanoTime();
			elapsedTime = endTime-initTime;
			double elapsedTimeInSeconds=elapsedTime/1000000000.0 ;
			serverLog.info("Global Restoration time: " + elapsedTimeInSeconds+ " s");
			
			timesOfGS+= elapsedTimeInSeconds;
			System.out.println("Sending times: "+timesOfGS);
			notifyEnd();	
			Util.pause(5);
			System.exit(0);
		}
		return timeOfThisProcess;
	}
	
	/**
	 * This method configures and launches the logger.
	 * 
	 * @param int The id of the process.
	 */
	private void loggerSetUp(int processId) {
		try {
			serverLog = LoggerUtil.getAppLoggerInfo("Process" + processId, "Logs", "Server" + processId
					+ ".log");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	private String vmExecuteCommand(String vmname, String username,
			String password, String command, String parameters) {
		return Util.execute(configuration.getVirtualBoxHome() + "VBoxManage"
				// + Constants.SPACE + "--nologo"
				+ Constants.SPACE + " guestcontrol" + Constants.SPACE + vmname
				+ Constants.SPACE + "--username" + Constants.SPACE + username
				+ Constants.SPACE + "--password" + Constants.SPACE + password
				+ Constants.SPACE + "run" + Constants.SPACE + "--exe"
				+ Constants.SPACE + command //+ Constants.SPACE + "--wait-stdout"
				+ Constants.SPACE + "--" + Constants.SPACE + command
				+ Constants.SPACE + parameters);
		/*
		 * Depende de la version. En 4.3 execute en lugar de run; --image en
		 * lugar de --exe
		 * 
		 * VBoxManage guestcontrol Mint --username root --password carlos run
		 * --exe /sbin/iptables --wait-stdout -- /sbin/iptables -t mangle -L
		 */
	}
}
