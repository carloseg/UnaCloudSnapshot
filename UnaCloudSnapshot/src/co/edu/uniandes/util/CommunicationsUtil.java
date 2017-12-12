package co.edu.uniandes.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import org.apache.log4j.Logger;
/**
 * This class provides several methods related to communications.
 *
* @author Carlos Eduardo G�mez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/
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
//			String m = r.readLine();
			r.close();
			w.close();
			socket.close();
		} catch (IOException e) {
			Logger log;
			try {
				log = LoggerUtil.getAppLoggerInfo("Comm-Util", "Logs", "Comm-Util.log");
				log.debug(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		return true;
	}

	/**
	 * This method uses aws API to know the public IP of the current host.
	 * @return a String with the IP
	 */
	
	public static String myIP() {
		String result = "";
		
		String ip ="";
		
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com/");
			URLConnection connection = whatismyip.openConnection();
		    connection.addRequestProperty("Protocol", "Http/1.1");
		    connection.addRequestProperty("Connection", "keep-alive");
		    connection.addRequestProperty("Keep-Alive", "1000");
		    connection.addRequestProperty("User-Agent", "Web-Agent");

		    BufferedReader in = 
		        new BufferedReader(new InputStreamReader(connection.getInputStream()));

		    ip = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	  
		return ip;
		// return "localhost";
	}
}
