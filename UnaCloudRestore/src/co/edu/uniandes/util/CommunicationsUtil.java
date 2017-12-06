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
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class CommunicationsUtil {

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

	// hay que revisar que entregue la IP adecuada. 
	// Por ahora solo entrega la primera y esto puede fallar.
//	public static String myIP() {
//		String result = "";
//		int n = 0;
//		try {
//            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//            while (interfaces.hasMoreElements()) {
//                NetworkInterface interfaz = interfaces.nextElement();
//                // No necesitamos el Loopback
//                if (interfaz.isLoopback()) {
//                    continue;
//                }
//                Enumeration<InetAddress> direccion = interfaz.getInetAddresses();
//                while (direccion.hasMoreElements()) {
//                    InetAddress ip = direccion.nextElement();
//                    // Solo IPv4
//                    if (ip instanceof Inet6Address) {
//                        continue;
//                    }
//                    result += ip.getHostAddress();
//                    break; // ESTA LINEA ES NUEVA
//                }
//                break;
//            }
//        } catch(SocketException e) {
//            System.out.println(e);
//        }
//		
////		result = "128.213.48.218";
//		result = "128.213.23.13";
//		
//		return result;
//	}

	// hay que revisar que entregue la IP adecuada. 
	// Por ahora solo entrega la primera y esto puede fallar.
	public static String myIP() {
		String result = "";
		int n = 0;
		try {
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
                    if (ip.getHostAddress().startsWith("10.0")) {
                        result += ip.getHostAddress();
                    }
                    break;
                }
                break;
            }
        } catch(SocketException e) {
            System.out.println(e);
        }
		
		return result;
	}
}
