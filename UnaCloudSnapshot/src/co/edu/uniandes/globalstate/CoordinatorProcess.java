package co.edu.uniandes.globalstate;

/*
 * CoordinatorProcess.java
 * 
 * This class implements an adaptation of the Violin Snapshot Algorithm, which is based on
 * the well known Mattern's Global Snapshot Algorithm.
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

	/**
	 * This is the constructor.
	 * @param id It is the id of the process.
	 * @param configuration It is the general configuration of the application.
	 * It includes the values contained in the properties file.
	 */
	public CoordinatorProcess(int processId, Configuration configuration,String vmName) {
		this.configuration = configuration;
		this.processId = processId;
		
		this.vm = new VM(configuration);
		
		int offset = configuration.getOffset();		
		
		int vmNumber = offset + processId;
		
		/**if (vmNumber<10) {
			vmname = configuration.getVmname() + "0" + vmNumber;
		} else {
			vmname = configuration.getVmname() + vmNumber;
		}*/
		vmname = vmName;
				
//		vmname = configuration.getVmname();
		
		done = new boolean[configuration.getMax()];

		loggerSetUp(processId);
		serverLog.info("Process " + processId + " is running ...");

		localPort = configuration.getBase() + processId;
	}


	/**
	 * This method starts the process server mode. It listens to a port waiting for
	 * messages to implement the global snapshot algorithm.
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
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				String line = reader.readLine();
				serverLog.info("Received: " + line);

				// A read message can be:
				// TAKE_SNAPSHOT + SPACE + id or    
				// DONE + SPACE + id
				
				// Sender identification no matter what kind of message has been received.
				int sender = Integer.parseInt(line.split(Constants.SPACE)[1]);

				// If it is the TAKE_SNAPSHOT message (it does not apply for the starter):
				if (line.startsWith(Constants.TAKE_SNAPSHOT) == true) {
					
//					String answer = NamesUtil.nameInitialTime(
//							processId,
//							configuration.getNameServerHostName(), 
//							configuration.getNameServerPort(), 1);
					
					// ***** modificar para que retorne el tiempo y enviar el tiempo local
					// con el mensaje DONE
					long t = step1();
					
					// Registrar el tiempo de terminación del snapshot
//					answer = NamesUtil.nameEndTime(
//							processId, 
//							configuration.getNameServerHostName(), 
//							configuration.getNameServerPort(), 1);
					
					// It sends a DONE message to the starter process
					String done = Constants.DONE + Constants.SPACE + processId + Constants.SPACE + t;
					
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
					if (line.startsWith(Constants.DONE) == true) {
						// It marks the sender process of the message
						markDone(sender);
						
						// If all peers are marked, it execute the step 2.
						if (numberOfDone() == systemSize - 1) {
//							step2();
							serverLog.info("The global snapshot has finished.");

							endTime = System.nanoTime();
							long elapsedTime = endTime-initTime;
//							System.out.println("Global Snapshot time: " + elapsedTime/1000000000.0 + " s");
							serverLog.info("Global Snapshot time: " + elapsedTime/1000000000.0 + " s");
							
//							elapsedTime = ta-initTime;
//							System.out.println("Antes del Local Snapshot time: " + elapsedTime/1000000000.0 + " s");
							
							
//							elapsedTime = tc-tb;
//							System.out.println("Después del Local Snapshot time: " + elapsedTime/1000000000.0 + " s");
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
		String list = NamesUtil.nameQueryList(processId, configuration.getNameServerHostName(),
				configuration.getNameServerPort());

		if (list.equals("") == false) {
			String[] server = list.split(Constants.SEMICOLON);

			for (int i = 0; i < server.length; i++) {
				send(server[i], message);
			}
		}
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
	
//	/**
//	 * This method sets up the localStateFlag.
//	 * 
//	 * @param value The new value for the flag.
//	 */
//	public void setFlag(boolean value) {
//		snapshotFlag = value;
//	}

	/**
	 * This method marks a process indicating that its local snapshot is done.
	 * 
	 * @param index The process id.
	 */
	public void markDone(int index) {
		done[index] = true;
	}

	/**
	 * This method reports if the specified process has taken its local snapshot or not.
	 * 
	 * @param index The process id.
	 *            
	 * @return boolean The state of the process.
	 */
	public boolean isDone(int index) {
		return done[index];
	}

	/**
	 * This method reports the number of processes that have taken their local snapshots.
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

		serverLog.info("Starting the global snapShot");
		initTime = System.nanoTime();
		
		// It queries the number of processes in the system
		systemSize = Integer.parseInt(
				NamesUtil.nameQuerySize(
						processId, 
						configuration.getNameServerHostName(), 
						configuration.getNameServerPort()));
		
		String takeSnapshot = Constants.TAKE_SNAPSHOT + Constants.SPACE + processId;
		
//		String answer = NamesUtil.nameInitialTime(
//				processId,
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);

		serverLog.info("Broadcasting " + takeSnapshot);
		broadcast(takeSnapshot);
		
		step1();
		// Registrar el tiempo de terminación del snapshot
//		answer = NamesUtil.nameEndTime(
//				processId, 
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);
	}

	private long step1() {
		String s;
		//1. mark PRE 
		s = markPre(vmname, "root", "carlos");
		
		// 2. drop POST 
		s = dropPost(vmname, "root", "carlos");
		
		// 3. take the local snapshot
		serverLog.info("Starting the local snapShot of "+ vmname);

//		String answer = NamesUtil.nameInitialTime(
//				processId,
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);
		
		ta = System.nanoTime();
		s = vm.takeSnapshot(vmname, "snapshot");
		tb = System.nanoTime();
		
		long elapsedTime = tb-ta;
		
//		System.out.println("Local Snapshot time: " + elapsedTime/1000000000.0 + " s");
		serverLog.info("Local Snapshot time: " + elapsedTime/1000000000.0 + " s");
		//		answer = NamesUtil.nameEndTime(
//				processId, 
//				configuration.getNameServerHostName(), 
//				configuration.getNameServerPort(), 1);

		serverLog.info("Ending the local snapShot of "+ vmname);
		
		// 4. mark POST 
		s = markPost(vmname, "root", "carlos");

		// 5. accept POST
		s = acceptPost(vmname, "root", "carlos");
//		tc = System.nanoTime();
//		serverLog.info("Time after local snapShot of "+ vmname);
		return elapsedTime;
	}
	
	private void step2() {
		String s;
		//6. no mark.		
		s = noMark("Mint", "root", "carlos");
		
		// 7. accept POST. It removes the rule.	
		s = acceptPost("Mint", "root", "carlos");
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
	
	/////// QUITAR -p tcp
	
	// 1. 
	private String markPre(String vmname, String user, String password) {
		String s;
		// pone la marca 0x10 (PRE)
		s = vmExecuteCommand(vmname, user, password, "/sbin/iptables",
				"-t mangle -A OUTPUT -p tcp -j DSCP --set-dscp 16");
		return s;
	}

	// 2. 
	private String dropPost(String vmname, String user, String password) {
		String s;
		// agrega la regla de descartar paquetes entrantes con la marca 0x10
		s = vmExecuteCommand(vmname, user, password, "/sbin/iptables",
				"-A INPUT -p tcp -m dscp --dscp 32 -j DROP");
		return s;
	}

	// 4. 	
	private String markPost(String vmname, String user, String password) {
		String s;
		// pone la marca 0x20 (POST)
		s = vmExecuteCommand(vmname, user, password, "/sbin/iptables",
				"-t mangle -R OUTPUT 1 -p tcp -j DSCP --set-dscp 32");
		return s;
	}

	// 5. 
	private String acceptPost(String vmname, String user, String password) {
		String s;
		s = vmExecuteCommand(vmname, user, password, "/sbin/iptables",
		"-D INPUT 1");
		return s;
	}

	// 6. 
	private String noMark(String vmname, String user, String password) {
		String s;
		s = vmExecuteCommand(vmname, user, password, "/sbin/iptables",
		"-t mangle -F");
		return s;
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
