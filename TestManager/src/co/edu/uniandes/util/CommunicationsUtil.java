package co.edu.uniandes.util;

/**
 * This class provides several methods related to communications.
 *
* @author Carlos Eduardo Gómez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class CommunicationsUtil {
	
	/**
	 * This method sends a message to specified server using the TCP protocol. It does not
	 * keep the connection nor wait for an answer. It returns a boolean value depending on
	 * the success or failure of the procedure.
	 * 
	 * @param message It is the message to send.
	 * @param hostnameServer It is the hostname of the destination server.
	 * @param port It is the port number used by the destination server to listen to.
	 * 
	 * @return boolean The success of failure of the procedure.
	 */
	public static boolean sendMessage(String message, String hostnameServer, int port) {
		try {
			Socket socket = new Socket(hostnameServer, port);
			BufferedReader r = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter w = new PrintWriter(socket.getOutputStream(), true);
			w.println(message);
			r.close();
			w.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
