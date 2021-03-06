package co.edu.uniandes.util;

/**
 * This class provides several methods related to the name service.
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NamesUtil {
	/**
	 * This method sends a request insert message to a specified name server. The name
	 * server registers the process and assigns a clientId. 
	 * It returns the answer sent to by the name server.
	 * 
	 * @param address It is the address of the sender process.
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameInsert(String address, int localPort, String nameServer, int port) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "INSERT" + Constants.SPACE +
				address + Constants.SPACE + localPort;
		
		a = sendMessage(nameServer, port, m);
		
		return a;
	}
	
	public static String notifyProcessOisReady(String address, String nameServer, int port){
		
		String m = "Process0 is ready." + Constants.SPACE +address;
		
		return sendMessage(nameServer, port, m);
		
	}

	/**
	 * This method sends a query message to a specified name server. The request is for a
	 * specific registered process. It returns the answer sent to by the name server.
	 * 
	 * @param name It is the name of a registered process in the name server.
	 * @param clientId It is the id of the sender process.
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameQuery(String name, int clientId, String nameServer, int port) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "QUERY" + Constants.SPACE +
				name + Constants.SPACE +
				clientId;

		a = sendMessage(nameServer, port, m);
		
		// clientId:address:localPort
		return a;
	}

	/**
	 * This method sends a query message to a specified name server. The request is for the
	 * list of registered processes without considering the sender process of the request. 
	 * It returns the answer sent to by the name server.
	 * 
	 * @param clientId It is the id of the sender process.
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameQueryList(int clientId, String nameServer, int port) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "QUERY_LIST" + Constants.SPACE + clientId;

		a = sendMessage(nameServer, port, m);
		
		// clientId:address:localPort
		return a;
	}
	
	public static void  nameQueryEnding(String times, String nameServer, int port){
		
		String m = "QUERY_END_GS" + Constants.SEMICOLON + times;
		sendMessage(nameServer, port, m);
	}

	/**
	 * This method sends a query message to a specified name server. The request is for the
	 * size of the directory with registered processes. It returns the answer sent to by 
	 * the name server.
	 * 
	 * @param clientId It is the id of the sender process.
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameQuerySize(int clientId, String nameServer, int port) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "QUERY_SIZE" + Constants.SPACE +
				clientId;

		a = sendMessage(nameServer, port, m);
		
		return a;
	}

	/**
	 * This method sends a message to register the initial time of the test.
	 * 
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameInitialTime(int clientId, String nameServer, int port, int n) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "INITIAL_TIME_" + n  + Constants.SPACE +
				clientId;

		a = sendMessage(nameServer, port, m);
		
		return a;
	}
	
	/**
	 * This method sends a message to register the end time of the test.
	 * 
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameEndTime(int clientId, String nameServer, int port, int n) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "END_TIME_" + n  + Constants.SPACE +
				clientId;

		a = sendMessage(nameServer, port, m);
		
		return a;
	}
	
	/**
	 * This method sends a message to reset the name server ring directory. (corregir)
	 * 
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameServerQueryTime(String nameServer, int port, int n) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "QUERY_TIME_" + n;

		a = sendMessage(nameServer, port, m);
		
		return a;
	}	
	
	/**
	 * This method sends a message to reset the name server directory.
	 * 
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @return String It is the answer sent by the name server.
	 */
	public static String nameServerReset(String nameServer, int port) {
		// a: answer; r: reader; w: writer
		String a = "";

		String m = "RESET";

		a = sendMessage(nameServer, port, m);
		
		return a;
	}

	/**
	 * This method sends a message to the specified name server.
	 * 
	 * @param nameServer It is the hostname of the name server.
	 * @param port It is the port number used by the name server to listen to.
	 * @param String It is the message to send.
	 * @return String It is the answer sent by the name server.
	 */
	private static String sendMessage(String nameServer, int port,
			String m) {
		String a = "";
		try {
			Socket socket = new Socket(nameServer, port);
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
	
	
}
