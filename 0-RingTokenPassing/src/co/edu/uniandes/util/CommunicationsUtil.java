package co.edu.uniandes.util;

/**
 * This class provides several methods related to communications.
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
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
			String m = r.readLine();
			r.close();
			w.close();
			socket.close();
		} catch (IOException e) {
			Logger log;
			try {
				log = LoggerUtil.getLoggerInfo("Comm-Util", "Logs", "Comm-Util.log");
				log.debug(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		return true;
	}

	// hay que revisar que entregue la IP adecuada. 
	// Por ahora solo entrega la primera y esto puede fallar.
	// Esto esta fallando
	public static String myIP() {
		String result = "";
		/**try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface interfaz = interfaces.nextElement();
                // No necesitamos el Loopback
                if (interfaz.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> direccion = interfaz.getInetAddresses();
                
                while (direccion.hasMoreElements()) {
                    InetAddress ip = direccion.nextElement();
                    // Solo IPv4
                    
                    if (ip instanceof Inet6Address) {
                        continue;
                    }
                    
                    result = ip.getHostAddress();
                    break;
                }
                break;
            }
        } catch(SocketException e) {
            System.out.println(e);
        }*/
		
		//return result;
		
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
	  //return "localhost";
	}
}
