package co.edu.uniandes.main;

/**
 * This application offers a name server service.
 * 
 * A process (server) registers itself at the name server giving its ip address.
 * The name server assigns it a processId, which is returned into the answer.
 * The properties have read from a properties file. This is the main class. 
 * 
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import co.edu.uniandes.configuration.NameServerConfiguration;
import co.edu.uniandes.tests.TestTime;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.LoggerUtil;

public class MetadataServer {
	private NameServerConfiguration configuration;
	private ServerSocket listener;
	private Socket client;
	private BufferedReader reader;
	private PrintWriter writer;
	private HashMap<String, String> directory;
	private HashMap<String, TestTime> testTime;
	private Logger log;

	/**
	 * This is the constructor
	 */
	public MetadataServer() {
		// reading the configuration properties
		configuration = new NameServerConfiguration("metadataServer.properties");
		loggerSetUp();

		log.info("Metadata Server is running ...");
		directory = new HashMap<String, String>();
		testTime = new HashMap<String, TestTime>();
	}

	/**
	 * This method initiates the listener and processes all messages.
	 */
	private void init() {
		try {
			// listener creation
			listener = new ServerSocket(configuration.getNameServerPort());
			while (true) {
				// blocking waiting for processes
				client = listener.accept();
				
				String add = (client.getInetAddress()+"").substring(1);

				// connections creation
				plugIn();

				// reading the message sent by the process
				String line = reader.readLine();
				log.debug("Received: " + line);

				String answer = "";

				// messages processing
				if (line.trim().startsWith("INSERT")) {
					answer = insert(line, add); 
				} else if (line.startsWith("QUERY" + Constants.SPACE)) {
					answer = query(line);
				} else if (line.trim().startsWith("QUERY_LIST")) {
					answer = queryList(line);
				} else if (line.trim().startsWith("QUERY_SIZE")) {
					answer = querySize(line);
				} else if (line.trim().startsWith("INITIAL_TIME")) {
					answer = initialTime(line);
				} else if (line.trim().startsWith("END_TIME")) {
					answer = endTime(line);
				} else if (line.trim().startsWith("QUERY_TIME")) {
					answer = queryTime();
				} else if (line.trim().startsWith("RESET")) {
					answer = reset();
				}
				
				// elimination of the ; at the end of the string
				if (answer.endsWith(Constants.SEMICOLON) == true) {
					answer = answer.substring(0, answer.length() - 1);
				}
				writer.println(answer);
				log.debug("Sent: " + answer);
				if (directory.isEmpty()==true){
					log.debug("Directory is empty.");
				} else {
					log.debug("Directory content: " + directory.toString());
				}
			}
		} catch (Exception e) {
			System.err.println("Exception caught:" + e);
		} finally {
			closeConnection();
		}
	}
	
	/**
	 * This method insert the process. The name server assigns a processId and insert a
	 * register about the process.
	 * 
	 * @param line
	 * @return answer
	 */
	private String insert(String line, String add) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// INSERT --> 0
		// address --> 1
		int processId = directory.size();
		
		String address = "";
		
		if (add.equals("127.0.0.1") == true) {
			address = configuration.getMyIP();
		} else {
			address = add;
		}

		String name = configuration.getProcessHostnamePrefix()
				+ processId;
		int localPort = configuration.getBase() + processId;

		if (directory.containsKey(name)) {
			answer = "The server name is already registered.";
		} else {
			// The name server store a key-value pair
			// key = the name of the process
			// value = processId:address:localPort
			
//			System.out.print("address: " + address);
//			System.out.println(" port: " + localPort);
			directory.put(name, processId + Constants.COLON
					+ address + Constants.COLON + localPort);

			answer = "OK. ProcessId = " + processId + " name - " + address + " - " + localPort;
			log.info("Sent: " + answer);
//			log.info("Directory state: " + directory.toString());

			long time = System.nanoTime();
			TestTime t = new TestTime();
			t.setInitialTime(time);
			
			testTime.put(name, t);
			answer += " Initial Time Registered";
		}
		return answer;
	}

	/**
	 * This method returns the information of the requested registered process.
	 * 
	 * @param line
	 * @return answer
	 */
	private String query(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// QUERY --> 0
		// nameQueried --> 1
		// sender (clientId) --> 2
		String name = m[1];
		String sender = m[2];
		log.debug("Name resolution query requested by "
				+ sender + ": " + line);
		if (directory.containsKey(name)) {
			answer = directory.get(name);
		} else {
			answer = "The server " + name + " is not found.";
		}
		return answer;
	}
	
	/**
	 * This method returns the list of registered processes without 
	 * including the process that requested it
	 * 
	 * @param line
	 * @return answer
	 */
	private String queryList(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// QUERY_LIST --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		log.debug("List query requested by " + sender + ": "
				+ line);

		// iterating over the hashmap
		Iterator<Map.Entry<String, String>> it = directory
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) it
					.next();

			String temp = e.getValue() + "";
			// if (sender != processId)
			if (temp.split(":")[0].equals(sender) == false) {
				answer += temp + Constants.SEMICOLON;
			}
		}
//		System.out.println(answer);
		return answer;
	}

	/**
	 * This method returns the number of registered processes.
	 * 
	 * @param line
	 * @return answer
	 */
	private String querySize(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// QUERY_SIZE --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		log.debug("Query size sent by " + sender + ": "
				+ line);

		int size = directory.size();

		answer = size + "";
		return answer;
	}

	/**
	 * This method sets the initial time of a test of a process.
	 * 
	 * @param line
	 * @return answer
	 */
	private String initialTime(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// INITIAL_TIME --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		String name = "serv#" + sender;

		long time = System.nanoTime();
		TestTime t = testTime.get(name);
		t.setInitialTime(time);
		answer = "OK. Initial Time Registered for ProcessId = " + sender;
//		TestTime t = new TestTime();
//		t.setInitialTime(time);
//		testTime.put(name, t);
		answer = "OK. Initial Time " + time + " Registered for ProcessId = "
				+ sender;
		return answer;
	}

	/**
	 * This method sets the end time of a test of a process.
	 * 
	 * @param line
	 * @return answer
	 */
	private String endTime(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// END_TIME --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		String name = "serv#" + sender;

		long time = System.nanoTime();
		TestTime t = testTime.get(name);
		t.setEndTime(time);
		answer = "OK. End Time Registered for ProcessId = " + sender;
		return answer;
	}
	
	/**
	 * This method returns the elapsed time of all registered processes.
	 * 
	 * @return answer
	 */
	private String queryTime() {
		String answer = "";
		
		// iterating over the hashmap
		Iterator<Map.Entry<String, TestTime>> it = testTime
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, TestTime> e = (Map.Entry<String, TestTime>) it.next();

			String temp = e.getKey() + "";
			long t1 = testTime.get(temp).getInitialTime();
			long t2 = testTime.get(temp).getEndTime();

			double duration = (t2 - t1)/1000000000.0;
			answer += "La prueba de " + temp + " duró: " + duration + " segundos" + Constants.SEMICOLON;
		}
		
//		System.out.println(answer);
		return answer;
	}

	/**
	 * This method reset the directory and the testTime hashmaps.
	 * 
	 * @return answer
	 */
	private String reset() {
		directory.clear();	
		testTime.clear();
		log.info("Name server reset");

		return "OK";
	}

	/**
	 * This method creates the streams for using in the application.
	 * 
	 * @throws IOException
	 */
	private void plugIn() throws IOException {
		reader = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		writer = new PrintWriter(client.getOutputStream(), true);
	}

	/**
	 * This method closes streams and sockets.
	 */
	private void closeConnection() {
		try {
			writer.close();
			reader.close();
			client.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * This method setups the loggers used in this class.
	 */
	private void loggerSetUp() {
		try {
			String logType = configuration.getLogType();
			if (logType.equals("info") == true) {
				log = LoggerUtil.getLoggerInfo(
						configuration.getLabelLogFile(),
						configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("debug") == true) {
				log = LoggerUtil.getLoggerDebug(
						configuration.getLabelLogFile(),
						configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("both") == true) {
				log = LoggerUtil.getLoggers(
						configuration.getLabelLogFile(),
						configuration.getPathLog(),
						configuration.getLogFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is the main method to launch the name server.
	 */
	public static void main(String args[]) {
		MetadataServer ns = new MetadataServer();
		ns.init();
	}
}