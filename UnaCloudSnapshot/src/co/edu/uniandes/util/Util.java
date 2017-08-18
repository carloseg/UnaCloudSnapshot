package co.edu.uniandes.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Util {
	private static final String SPACE = " ";
	private static final String COLON = ":";
	/**
	 * Este metodo ejecuta un comando en el sistema operativo del host donde corre el cliente.
	 * @param command Es el comando a ejecutar.
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
	 * Este metodo pausa la aplicacion unos segundos.
	 * @param n Es el numero de segundos.
	 */	
	public static void pause(long n) {
		try {
			Thread.sleep(n * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Este metodo pausa la aplicacion unos milisegundos.
	 * @param n Es el numero de milisegundos.
	 */	
	public static void pauseMS(long n) {
		try {
			Thread.sleep(n );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Este metodo imprime un array.
	 * @param n Es el numero de milisegundos.
	 */	

	public static void printArray(boolean a[], int max) {
		for (int i=0; i<max; i++) {
			System.out.print("[" + i + "]: " + a[i] + "\t");
			if (i % 10 == 9) {
				System.out.println();
			}
		}
	}

}
