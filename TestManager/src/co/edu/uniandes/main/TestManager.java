package co.edu.uniandes.main;

/**
 * This application offers a manager service to an RTP application.
 * 
 * A process (server) registers itself at the name server giving its ip address.
 * The name server assigns it a processId, which is returned into the answer.
 * The properties have read from a properties file. This is the main class. 
 * 
 * @author Carlos Eduardo Gomez Montoya
 * 		   Jose Gabriel Tamura L
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import co.edu.uniandes.configuration.RTPTestManagerConfiguration;
import co.edu.uniandes.tests.TestTime;
import co.edu.uniandes.util.Constants;
import co.edu.uniandes.util.LoggerUtil;


import jxl.*;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class TestManager {
	private RTPTestManagerConfiguration configuration;
	private ServerSocket listener;
	private Socket client;
	private BufferedReader reader;
	private PrintWriter writer;
	private HashMap<String, String> directory;
	private HashMap<String, Integer> howManyFromThisIP;
	private Logger log;
	
	private long timeInitRing;
	private long timeFinRing;
	private int maxTokenValue;
	private int maxBenchmarkValue;
	private int registerExit;
	private int pauseToStartRing;
	private int pauseBenchmark;
	private int basePort;
	private String testLabel;
	
	private boolean blockEntryToDirectory;
	private String errorsOfTheCurrentRing;

	/**
	 * This is the constructor
	 */
	public TestManager() {
		// reading the configuration properties
		configuration = new RTPTestManagerConfiguration("tm.properties");

		// logger setup
		loggerSetUp();
		maxTokenValue = configuration.getMaxTokenValue();
		maxBenchmarkValue = configuration.getMaxBenchmarkValue();
		pauseToStartRing = configuration.getPauseToStartRing();
		pauseBenchmark = configuration.getPauseBenchmark();
		basePort  = configuration.getBasePort();
		testLabel = configuration.getTestLabel();
		log.info("Name Server is running ...");
		directory = new HashMap<String, String>();
		
		howManyFromThisIP = new HashMap<String,Integer>();
		
		timeInitRing = Long.MAX_VALUE;
		timeFinRing =0;
		
		blockEntryToDirectory=false;
		errorsOfTheCurrentRing = "";
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
				
				else if(line.trim().startsWith("RING_PROGRESS")){
					answer = "OK";
					String base = line.split(";")[1];
					System.out.println("\n"+base);
					
					if(base.split(":")[1].split("%")[0].trim().equals(configuration.getPercentageToStartGS())){
						String [] dir =configuration.getAdressAndPortOfMetaDataServer().split("-");
						sendMessage(dir[0], Integer.parseInt(dir[1]), "StartGS");
						log.debug("GS  starting ");
						System.out.println("Message sent to GS MetadataServer");
					}
					
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

		double duration = (timeFinRing-timeInitRing) / 1000000000.0;
		answer += "La prueba duró: " + duration
				+ " segundos" + Constants.SEMICOLON;
		System.out.println(answer + "\n");
		return answer;
	}
	private String allInitialTime(String line) {
		String answer = "";
		long time = System.nanoTime();

		timeInitRing = time;

		blockEntryToDirectory = true;
		log.info("\nThe ring just started. The ring is composed by "+directory.size() +" instances.");
		return answer;
	}
	
	//nunca se llama a este metodo
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
		//testTime.put(name, t);
		if(time < timeInitRing){
			timeInitRing = time;
		}
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
		
		String errors = m[2];
		if(!errors.equals("0")){
			errorsOfTheCurrentRing += name+":"+errors+" ";
		}

		long time = System.nanoTime();
		//TestTime t = testTime.get(name);
		//t.setEndTime(time);
		if(time > timeFinRing){
			timeFinRing = time;
		}
		answer = "OK. End Time " + time + " Registered for ProcessId = "
				+ sender;


		registerExit++;
		if(registerExit == directory.size()){
			System.out.println("\nAll finished");
			
			printErrors();
			printTotalTime();
			reset();
		}

		return answer;
	}
	/**
	 * This method creates a new file with name : Label TODO
	 */
	private void printErrors(){
		if(errorsOfTheCurrentRing.equals("")){
			return;
		}
		
		File errors = new File(testLabel+" Errors.txt");
		try{
			if(errors.exists()==false){
				System.out.println("We had to make a new file.");
				errors.createNewFile();
			}
			PrintWriter out = new PrintWriter(new FileWriter(errors, true));
			out.append("\n"+"******* " + new Date().toString() +"******* " );
			out.append(errorsOfTheCurrentRing+ "\n");
			out.close();
		}catch(IOException e){
			System.out.println("COULD NOT PRINT ERRORS");
		}
	}

	/**
	 * Print total time. Selects the smallest time and the biggest time in the testTime hash.
	 * Insert the value in TIMES of RTP.txt file (appends, not overwrite)
	 * With current date
	 */
	private void printTotalTime(){
		double duration = (timeFinRing-timeInitRing) / 1000000000.0;
		
		String result = duration+" s.     MaxTokenValue: "+maxTokenValue+". MaxBenchmarkValue: "+maxBenchmarkValue;
		System.out.println(result);
		File log = new File("TIMES of RTP.txt");
		try{
			if(log.exists()==false){
				System.out.println("We had to make a new file.");
				log.createNewFile();
			}
			PrintWriter out = new PrintWriter(new FileWriter(log, true));
			out.append("\n"+"******* " + new Date().toString()+"     " +testLabel +"******* " );
			out.append(result+ "\n");
			out.close();
		}catch(IOException e){
			System.out.println("COULD NOT LOG!!");
		}
		try{
			Workbook workbook = null;
			try{
				workbook = Workbook.getWorkbook(new File("resultados-Name-Server.xls"));
			}catch(Exception e){}
			
			ArrayList <String> previousValues = new ArrayList<String>();
            if (workbook != null) {
            	Sheet sheet = workbook.getSheet(0);
            	
            	int row =1;
            	Cell actualCell = sheet.getCell(0, row);
                //System.out.print(actualCell.getContents() + ":");  
            	while(!actualCell.getContents().equals("-1")){
            		previousValues.add(actualCell.getContents()+"-"
            				+sheet.getCell(1, row).getContents()+"-"
            				+sheet.getCell(2, row).getContents()+"-"
            				+sheet.getCell(3, row).getContents()+"-"
            				+sheet.getCell(4, row).getContents());
            		row++;
            		actualCell = sheet.getCell(0,row);
            	}
                
                workbook.close();
            }
            
            WritableWorkbook workbookF = Workbook.createWorkbook(new File("resultados-Name-Server.xls"));

			WritableSheet sheetF = workbookF.createSheet("Pruebas", 0);

			//						C  F   M
			Label label = new Label(0, 0, "Max token value"); 
			sheetF.addCell(label); 
			label = new Label(1, 0, "Max benchmark value"); 
			sheetF.addCell(label); 
			label = new Label(2, 0, "Duración en segundos"); 
			sheetF.addCell(label);
			label = new Label(3, 0, "Label"); 
			sheetF.addCell(label);
			label = new Label(4, 0, "Fecha de prueba"); 
			sheetF.addCell(label);
			
			//add the previous values
			for(int i=0; i< previousValues.size();i++){
				String [] parts = previousValues.get(i).split("-");
				label = new Label(0,i+1 ,parts[0]);
				sheetF.addCell(label); 
				label = new Label(1,i+1 ,parts[1]);
				sheetF.addCell(label); 
				label = new Label(2,i+1 ,parts[2]);
				sheetF.addCell(label); 
				label = new Label(3,i+1 ,parts[3]);
				sheetF.addCell(label); 
				label = new Label(4,i+1 ,parts[4]);
				sheetF.addCell(label); 
				
			}
			int size =1+previousValues.size();
			label = new Label( 0, size, maxTokenValue+""); 
			sheetF.addCell(label); 
			label = new Label( 1, size, maxBenchmarkValue+""); 
			sheetF.addCell(label); 
			label = new Label( 2, size, duration+""); 
			sheetF.addCell(label);
			label = new Label( 3, size, testLabel); 
			sheetF.addCell(label);
			label = new Label( 4, size, new Date().toString()); 
			sheetF.addCell(label);
			
			//end of document
			label = new Label( 0, size+1, "-1"); 
			sheetF.addCell(label);
			
			
			workbookF.write();
	        workbookF.close();
		}
		catch(Exception e){
			e.printStackTrace();
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
		if(blockEntryToDirectory){
			return Constants.RING_IN_PROGRESS;
		}
		System.out.println("Method insert receives line: "+ line);
		String answer = "";
		String[] m = line.split(Constants.SPACE);
		// INSERT --> 0
		// address --> 1
		int processId = directory.size();
		String address = m[1];
		
		//Serv#0
		String name = configuration.getProcessHostnamePrefix() + processId;
		
		

		if (directory.containsKey(name)) {
			answer = "The server name is already registered.";
		} else {
			
			int localPort = basePort;
			
			try{
				int howManyFromIp = howManyFromThisIP.get(address);
				
				localPort += howManyFromIp+1;
				
				howManyFromThisIP.put(address, howManyFromIp+1);
			}
			catch(Exception e){
				//There is no resource from that ip yet
				
				howManyFromThisIP.put(address, 0);
				
			}
			
			// The name server store a key-value pair
			// key = the name of the process
			// value = processId:address:localPort
			directory.put(name, processId + Constants.COLON + address
					+ Constants.COLON + localPort);

			answer = "OK. ProcessId:" + processId+ ":"+maxTokenValue+":"+maxBenchmarkValue+":"+pauseToStartRing+":"+pauseBenchmark+":"+localPort;
			log.info("Sent: " + answer);	
		}
		
		//System.out.println(directory.toString());
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
		System.out.println("ID "+line.split(" ")[1]+"  is asking his next. Answer: "+answer);
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

		directory.clear();
		howManyFromThisIP.clear();
		
		timeInitRing = Long.MAX_VALUE;
		timeFinRing =0;
		
		registerExit =0;
		blockEntryToDirectory = false;
		errorsOfTheCurrentRing = "";

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
	
	
	private static String sendMessage(String address, int port,String m) {
		String a = "";
		try {
			Socket socket = new Socket(address, port);
			BufferedReader r = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter w = new PrintWriter(socket.getOutputStream(), true);
			w.println(m);
			a = r.readLine();
			r.close();
			w.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return a;
	}

	/**
	 * This is the main method to launch the name server.
	 */
	public static void main(String args[]) {
		TestManager ns = new TestManager();
		ns.init();
	}

}
