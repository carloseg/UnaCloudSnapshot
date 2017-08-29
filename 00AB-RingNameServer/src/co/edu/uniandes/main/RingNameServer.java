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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import co.edu.uniandes.configuration.NameServerConfiguration;
import co.edu.uniandes.tests.TestTime;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.LoggerUtil;

public class RingNameServer {
	private NameServerConfiguration configuration;
	private ServerSocket listener;
	private Socket client;
	private BufferedReader reader;
	private PrintWriter writer;
	private HashMap<String, String> directory;
	private HashMap<String, TestTime> testTime;
	private Logger log;
	private int registerExit;

	/**
	 * This is the constructor
	 */
	public RingNameServer() {
		// reading the configuration properties
		configuration = new NameServerConfiguration("nameServer.properties");

		// logger setup
		loggerSetUp();

		log.info("Name Server is running ...");
		directory = new HashMap<String, String>();
		testTime = new HashMap<String, TestTime>();
		registerExit =0;
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

				// connections creation
				plugIn();

				// reading the message sent by the process
				String line = reader.readLine();
				log.debug("Received: " + line);

				String answer = "";

				// messages processing
				/*
				 * ----------> INSERT The name server receives the ip address of
				 * the process. The name server assigns a processId and insert a
				 * register about the process.
				 */
				if (line.trim().startsWith("INSERT")) {
					answer = insert(line);
				} else if (line.trim().startsWith("QUERY_LIST")) {
					answer = queryList(line);
				} else if (line.trim().startsWith("QUERY_NEXT")) {
					answer = queryNext(line);
				} else if (line.trim().startsWith("QUERY_SIZE")) {
					answer = querySize(line);
				} else if (line.trim().startsWith("RING_RESET")) {
					answer = reset();						
				} else if (line.trim().startsWith("INITIAL_TIME")) {
					answer = initialTime(line);
				} else if (line.trim().startsWith("END_TIME")) {
					answer = endTime(line);
				} else if (line.trim().startsWith("QUERY_TIME")) {
					answer = queryTime();
				} else if (line.startsWith("QUERY" + Constants.SPACE)) {
					answer = query(line);
				} else if (line.trim().startsWith("All_INITIAL_TIME")) {
					answer = allInitialTime(line);
				}

				// elimination of the ; at the end of the string
				if (answer.endsWith(Constants.SEMICOLON) == true) {
					answer = answer.substring(0, answer.length() - 1);
				}
				writer.println(answer);
				log.debug("Sent: " + answer);
				if (directory.isEmpty() == true) {
					log.debug("Directory is empty.");
				} else {
					log.debug("Directory content: " + directory.toString());
				}
			}
		} catch (Exception e) {
			System.err.println("Exception caught:" + e);
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	private String queryTime() {
		String answer = "";

		// iterating over the hashmap
		Iterator<Map.Entry<String, TestTime>> it = testTime.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, TestTime> e = (Map.Entry<String, TestTime>) it
					.next();

			String temp = e.getKey() + "";
			long t1 = testTime.get(temp).getInitialTime();
			long t2 = testTime.get(temp).getEndTime();

			double duration = (t2 - t1) / 1000000000.0;
			answer += "La prueba de " + temp + " duró: " + duration
					+ " segundos" + Constants.SEMICOLON;
		}

		System.out.println(answer + "\n");
		return answer;
	}

	private String allInitialTime(String line) {
		String answer = "";

		long time = System.nanoTime();

		// iterating over the hashmap
		Iterator<Map.Entry<String, String>> it = directory.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) it.next();

			TestTime t = new TestTime();
			t.setInitialTime(time);

			String temp = e.getKey() + "";

			testTime.put(temp, t);
		}

		System.out.println(answer + "\n");
		return answer;
	}

	private String initialTime(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// INITIAL_TIME --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		String name = "serv#" + sender;

		long time = System.nanoTime();
		TestTime t = new TestTime();
		t.setInitialTime(time);
		testTime.put(name, t);
		answer = "OK. Initial Time " + time + " Registered for ProcessId = "
				+ sender;
		return answer;
	}

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
		answer = "OK. End Time " + time + " Registered for ProcessId = "
				+ sender;


		registerExit++;
		if(registerExit == directory.size()){
			System.out.println("All finished");

			printTotalTime();
			reset();
		}

		return answer;
	}

	/**
	 * Print total time. Selects the smallest time and the biggest time in the testTime hash.
	 * Insert the value in TIMES of RTP.txt file (appends, not overwrite)
	 * With current date
	 */
	private void printTotalTime(){
		long smallest = Long.MAX_VALUE;
		long biggest =  Long.MIN_VALUE;

		Iterator<Map.Entry<String, TestTime>> it = testTime.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, TestTime> e = (Map.Entry<String, TestTime>) it
					.next();

			String temp = e.getKey() + "";
			long t1 = testTime.get(temp).getInitialTime();
			long t2 = testTime.get(temp).getEndTime();

			if(smallest > t1){
				smallest = t1;
			}
			if(biggest < t2){
				biggest = t2;
			}
		}
		double duration = (biggest - smallest ) / 1000000000.0;
		String result = duration+" s";
		System.out.println(result);
		File log = new File("TIMES of RTP.txt");
		try{
			if(log.exists()==false){
				System.out.println("We had to make a new file.");
				log.createNewFile();
			}
			PrintWriter out = new PrintWriter(new FileWriter(log, true));
			out.append("\n"+"******* " + new Date().toString() +"******* " );
			out.append(result+ "\n");
			out.close();
		}catch(IOException e){
			System.out.println("COULD NOT LOG!!");
		}
	}
	/**
	 * This method insert the process. The name server assigns a processId and
	 * insert a register about the process.
	 * 
	 * @param line
	 * @return answer
	 */
	private String insert(String line) {
		System.out.println("Method insert receives line: "+ line);
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// INSERT --> 0
		// address --> 1
		int processId = directory.size();
		String address = m[1];

		String name = configuration.getProcessHostnamePrefix() + processId;
		int localPort = configuration.getBase() + processId;

		if (directory.containsKey(name)) {
			answer = "The server name is already registered.";
		} else {
			// The name server store a key-value pair
			// key = the name of the process
			// value = processId:address:localPort
			directory.put(name, processId + Constants.COLON + address
					+ Constants.COLON + localPort);

			answer = "OK. ProcessId = " + processId;
			log.info("Sent: " + answer);
		}
		return answer;
	}

	/**
	 * This method returns the list of registered processes without including
	 * the process that requested it
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
		log.debug("List query requested by " + sender + ": " + line);

		// iterating over the hashmap
		Iterator<Map.Entry<String, String>> it = directory.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> e = (Map.Entry<String, String>) it.next();

			String temp = e.getValue() + "";
			// if (sender != processId)
			if (temp.split(":")[0].equals(sender) == false) {
				answer += temp + Constants.SEMICOLON;
			}
		}
		return answer;
	}

	/**
	 * This method returns the information of the next registered process based
	 * on the idProcess. Next process for the last one is the first registered
	 * process. This first version considers only consecutive numbers, and does
	 * not consider if the directory is empty.
	 * 
	 * @param line
	 * @return answer
	 */
	private String queryNext(String line) {
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// QUERY_NEXT --> 0
		// sender (clientId) --> 1
		String sender = m[1];
		log.debug("Next resolution query requested by " + sender + ": " + line);

		// next in a logical ring
		int next = (Integer.parseInt(sender) + 1) % directory.size();
		String name = "serv#" + next;

		answer = directory.get(name);
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
		log.debug("Query size sent by " + sender + ": " + line);

		int size = directory.size();

		answer = size + "";
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
		log.debug("Name resolution query requested by " + sender + ": " + line);
		if (directory.containsKey(name)) {
			answer = directory.get(name);
		} else {
			answer = "The server name is not found.";
		}
		return answer;
	}

	/**
	 * This method reset the directory.
	 * 
	 * @return answer
	 */
	private String reset() {

		directory = new HashMap<String, String>();
		testTime = new HashMap<String, TestTime>();
		registerExit =0;

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
	public void closeConnection() {
		try {
			writer.close();
			reader.close();
			client.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void loggerSetUp() {
		try {
			String logType = configuration.getLogType();
			if (logType.equals("info") == true) {
				log = LoggerUtil.getLoggerInfo(configuration.getLabelLogFile(),
						configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("debug") == true) {
				log = LoggerUtil.getLoggerDebug(
						configuration.getLabelLogFile(),
						configuration.getPathLog(),
						configuration.getLogFileName());

			} else if (logType.equals("both") == true) {
				log = LoggerUtil.getLoggers(configuration.getLabelLogFile(),
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
		RingNameServer ns = new RingNameServer();
		ns.init();
	}

}
