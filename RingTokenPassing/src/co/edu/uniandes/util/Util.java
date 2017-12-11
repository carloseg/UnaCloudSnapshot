package co.edu.uniandes.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class provides several methods that can be useful.
 *
* @author Carlos Eduardo Gómez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/

public class Util {
	/**
	 * This method execute a command on the host operating system.
	 * @param command It is the command to execute.
	 * @return String It is the answer of the operating system as a consequence
	 * of the execution.
	 */
	public static String execute(String command) {
		Runtime r = Runtime.getRuntime ( );
		Process p = null;
		String text="";
		
		try
		{
			p = r.exec ( command );

			String line;

			BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

			while ((line = input.readLine()) != null) {
				text += line + "\r\n";
			}
			input.close();	
		}
		catch ( Exception e )
		{
			System.err.println ( "Error ejecutando " + command );
		}
		return text;
	}

	/**
	 * This method causes a pause of a specified amount of seconds.
	 * @param long It is the time expressed in seconds.
	 */
	public static void pause(long n) {
		try {
			Thread.sleep(n * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method causes a pause of a specified amount of miliseconds.
	 * @param long It is the time expressed in miliseconds.
	 */
	public static void pauseMS(long n) {
		try {
			Thread.sleep(n );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
