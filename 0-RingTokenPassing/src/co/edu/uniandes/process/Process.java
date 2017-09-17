package co.edu.uniandes.process;

/**
 * This is the process of a ring token passing application. 
 * 
 * A process is a server and a client at the same time. This class uses
 * a name server to know the network information needed to send messages
 * to the next process in the ring. The name server assigns the 
 * processId to each process when is registered. The first process in 
 * registering with the name server receives 0 as its processId, and that
 * process will be the initiator. It starts the ring with 0 as a first value 
 * and send a message (the token) with this value to the next process in the 
 * ring. The receiver process receives the token, reads the value, adds one 
 * to the value, and sends again the token to the next process in the ring. 
 * The properties have read from a properties file. This is the main class.
 * 
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.log4j.Logger;

import co.edu.uniandes.configuration.RingTokenPassingConfiguration;
import co.edu.uniandes.util.CommunicationsUtil;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.LoggerUtil;
import co.edu.uniandes.util.NamesUtil;
import co.edu.uniandes.util.Util;

/**
 * The class implements the Runnable interface because it is server and client
 * at the same time, so it launches two threads in parallel.
 */
public class Process implements Runnable {
	// These two values are assigned from the launcher.  
	// If processId == 0, this process is the initiator.
	private int processId;
	private RingTokenPassingConfiguration configuration;

	// attributes used to read properties from the properties file.
	private int maxTokenValue;
	private int maxBenchmarkValue;
	private int pauseMS;

	// attributes used to network communication.
	private ServerSocket listener;
	private int localPort;
	private Socket clientSideSocket;
	private Socket serverSideSocket;
	private BufferedReader clientSideBufferedReader;
	private BufferedReader serverSideBufferedReader;
	private PrintWriter clientSidePrintWriter;
	private PrintWriter serverSidePrintWriter;

	//	attributes used to write in a file for disk-writing intensive applications.
//	private FileWriter file;
//	private PrintWriter pwf;
	
	// other attributes.
	private boolean nextKnownFlag;
	private boolean flag;
	private String next;
	private boolean logFlag;
	private int expectedValue;
	private int errors;
	private int systemSize;

	private Logger log;

	/**
	 * This is the constructor
	 */
	public Process(int processId,int maxToken,int maxBenchmark,int pauseBenchmark,int basePort, RingTokenPassingConfiguration configuration) {
		// These two values are assigned from the launcher. If processId == 0, 
		// this process is the initiator.
		this.processId = processId;
		this.configuration = configuration;

		// the maximum token value is specified in the properties file
		maxTokenValue = maxToken;
		maxBenchmarkValue = maxBenchmark;
		pauseMS = pauseBenchmark;

		localPort = basePort + processId;
		loggerSetUp(processId, configuration);
		System.out.println("My local port is  (ID:" + processId + "): " + localPort);
		log.info("Server " + processId + " is running ...");
		
		flag = false;
		nextKnownFlag = false;
		logFlag = false;
		expectedValue = processId;
		errors = 0;

		// this lines will be used when the application is intensive in I/O
//		try {
//			file = new FileWriter("tokens"+processId+".txt");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		pwf = new PrintWriter(file);
	}

	/**
	 * This method initiates the execution of the thread.
	 */
	public void run() {
		String answer = "";
		
		// It is executed only by the initiator.
		initRing();

		try {
			// listener creation
			listener = new ServerSocket(localPort);

			// it is blocked waiting for processes (as clients)
			serverSideSocket = listener.accept();

			serverSideConnections();
			
			// It queries the number of processes in the system
			if (processId!=0) {
				systemSize = Integer.parseInt(
						NamesUtil.nameQuerySize(
								processId, 
								configuration.getNameServerHostName(), 
								configuration.getNameServerPort()));
			}

			while (true) {
				// reading the message sent by the process
				String line = serverSideBufferedReader.readLine();
				log.info("Message received: " + line);
				serverSidePrintWriter.println("OK");

				// parsing the token
				String[] parts = line.split(Constants.SPACE);
				String m1 = parts[0];
				int value = Integer.parseInt(parts[1]);
				String m2 = parts[2];

				// incrementing the value
				value++;

				// calculating the next expected value
				if (value != expectedValue) {
					errors++;
				}
				expectedValue += systemSize;


				// validating if the token value is allowed
				if (value >= maxTokenValue && maxTokenValue > 0) {
					m1 = "EXIT";
				}

				// updating the message with the new value and the sender
				// processId
				String newToken = m1 + Constants.SPACE + value
						+ Constants.SPACE + m2 + Constants.SPACE + processId;

				// obtaining the network information of the next process in the ring.
				if (nextKnownFlag == false) {
					getNext();
					clientSideConnections();
					nextKnownFlag = true;
				}

				log.info("Message sent: " + newToken);
				// sending the token
				clientSidePrintWriter.println(newToken);
				// write here the value of the token in the file. 
				// this must be configurable for disk-writing intensive applications.
				//
				//
				//
				//
				//pwf.println(value);
				benchmark();
				
				// if the token label is EXIT, the execution finalizes after a
				// pause
				if (m1.equals("EXIT") == false) {
					String m = clientSideBufferedReader.readLine();
				} else {
					if (processId!=0) {
					}
				}
				
				// if the token label is EXIT, the execution finalizes after a pause.
				if (m1.equals("EXIT") == true) {
					log.info("Leaving ...");
					System.out.println("Errors (ID:" + processId + "): " + errors);
					
					// It registers the end time of the execution.
					answer = NamesUtil.nameRingEndTime(
							processId, 
							configuration.getNameServerHostName(), 
							configuration.getNameServerPort());

					Util.pause(4);
					if (clientSideSocket != null)
						clientSideSocket.close();
					//if (file != null) file.close();
					Util.pause(1);
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method creates the TCP connection between this process and the next process
	 * in the ring (from the server side). It uses the information stored in the attribute
	 * "next", parsing the address or name of the process and its local port.
	 * This method is executed only once per process.
	 */
	private void serverSideConnections() throws IOException {
		serverSideBufferedReader = new BufferedReader(
				new InputStreamReader(serverSideSocket.getInputStream()));
		serverSidePrintWriter = new PrintWriter(
				serverSideSocket.getOutputStream(), true);
	}

	/**
	 * This method creates the TCP connection between this process and the next process
	 * in the ring (from the client side). It uses the information stored in the attribute
	 * "next", parsing the address or name of the process and its local port.
	 * This method is executed only once per process.
	 */
	private void clientSideConnections() throws UnknownHostException, IOException {
		// parsing the answer which comes with this format to identify the
		// address and port
		// of the next process to send the token
		String[] aServer = next.split(Constants.COLON);

		// sender:address:localPort (it can be hostname instead of address)
		// sender is the processId of the sender process --> 0
		// it is not used so far
		// process address that the process is asking tp the the name server -->
		// 1
		// process localport is the localport where the process is listen to as
		// a server --> 2

		String remoteName = aServer[1];
		int remotePort = Integer.parseInt(aServer[2]);

		// se crea el socket y se obtienen los flujos
		clientSideSocket = new Socket(remoteName, remotePort);
		clientSideBufferedReader = new BufferedReader(new InputStreamReader(
				clientSideSocket.getInputStream()));
		clientSidePrintWriter = new PrintWriter(
				clientSideSocket.getOutputStream(), true);
	}

	/**
	 * This method queries the network information for the next process in the ring.
	 * This method is executed only once per process.
	 */
	private void getNext() {
		// asking to the name server about the next process in the ring
		next = NamesUtil.nameQueryNextNode(processId,
				configuration.getNameServerHostName(),
				configuration.getNameServerPort());
		
		System.out.println(next);
	}

	/**
	 * This method initiates the ring. This method is executed only once.
	 */
	private void initRing() {
		// if the flag == true, send the first token
		String m = "";
		if (flag == true) {
			log.info("Starting the ring ...");
			System.out.println("Starting the ring ...");
			
			// It registers the initial time of the execution.
			String answer = NamesUtil.nameRingInitialTime(
					configuration.getNameServerHostName(), 
					configuration.getNameServerPort());
			
			// Solicita al NameServer el tamaño del anillo (ya entraron todos los participantes)
			systemSize = Integer.parseInt(
					NamesUtil.nameQuerySize(
							processId, 
							configuration.getNameServerHostName(), 
							configuration.getNameServerPort()));

			// Para validar si la ejecución va bien
			// 0 is the first value for the token
			int value = 0;
			if (value != expectedValue) {
				errors++;
			}
			expectedValue += systemSize;

			// write here the value of the token in the file. 
			// this must be configurable for disk-writing intensive applications.
			//
			//
			//
			//
			//pwf.println(value);

			// creating the token
			String token = "Hello" + Constants.SPACE + value + Constants.SPACE
					+ "sent-by" + Constants.SPACE + processId;

			try {
				// obtaining the network information of the next process in the ring.
				getNext();
				clientSideConnections();
				// sending the token to the next process
				log.info("Message sent: " + token);
				clientSidePrintWriter.println(token);
				m = clientSideBufferedReader.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// the flag is turned false
			flag = false;
			nextKnownFlag = true;
		}
	}

	/**
	 * This method performs a floating-point operation, which is a 
	 * multiplication of two random numbers, a number of times specified 
	 * in property maxBenchmarkValue.
	 */
	private void benchmark() {
		Random r = new Random();
		double a = -1.0;
		double b = -1.0;		
		double c = -1.0;
		
		for (int i=0; i<maxBenchmarkValue; i++) {
			a = r.nextDouble();
			b = r.nextDouble();
			c = a * b;
		}		
		//Util.pause(pauseMS);
	}
	
	/**
	 * This method sets up the flag with the specified value.
	 * 
	 * @param boolean The value to assign it to the flag.
	 */
	public void setFlag(boolean value) {
		flag = value;
	}
	
	/**
	 * This method configures and launches the logger.
	 * 
	 * @param int The id of the process.
	 * @param RingTokenPassingConfiguration The configuration set of values.
	 */
	private void loggerSetUp(int processId,
			RingTokenPassingConfiguration configuration) {
		// logger setup
		try {
			String logType = configuration.getLogType();
			if (logType.equals("info") == true) {
				log = LoggerUtil.getLoggerInfo(configuration.getLabelLogFile()
						+ processId, configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("debug") == true) {
				log = LoggerUtil.getLoggerDebug(configuration.getLabelLogFile()
						+ processId, configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("both") == true) {
				log = LoggerUtil.getLoggers(configuration.getLabelLogFile()
						+ processId, configuration.getPathLog(),
						configuration.getLogFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
