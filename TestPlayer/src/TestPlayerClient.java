/*
 * El objetivo de este programa es transferir un archivo desde el servidor al
 * cliente. El nombre del archivo es solicitado al usuario para que lo ingrese
 * por teclado. El cliente envía el nombre del archivo solicitado al servidor y
 * el servidor envía el archivo por fragmentos, si se hace necesario. El servidor
 * busca el archivo en la carpeta Shared. El cliente crea el archivo dentro de la
 * carpeta Test.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import co.edu.uniandes.configuration.ClientConfiguration;
import co.edu.uniandes.configuration.ServerConfiguration;
import co.edu.uniandes.util.Util;

public class TestPlayerClient
{
	public static final int    PORT = 20000;
	public static final String SERVER_LOCATION = "128.213.48.218";

	private Socket             clientSocket;
	private ObjectOutputStream outToServer;
	private ObjectInputStream  inFromServer;
	private FileOutputStream   out;
	private File               file;
	private ClientConfiguration configuration;
	
	// Método constructor.
	public TestPlayerClient () {
		
	}
	
	public void init ( )
	{
		// reading the configuration properties
		configuration = new ClientConfiguration("client.properties");

		String fileName = "";
		System.out.println ( "File Transfer Client" );

		try
		{
			clientSocket = new Socket ( SERVER_LOCATION, PORT );

			crearFlujos ( );
			send ("HELLO");
			
			String message = ( String ) receive ( );

			System.out.println(message);
			if (message.equals("PROPERTIES JAR RUN")) {
				fileName = ( String ) receive ( );
				receiveFile ( fileName );
				fileName = ( String ) receive ( );
				receiveFile ( fileName );
			} else if (message.equals("PROPERTIES RUN")) {
				fileName = ( String ) receive ( );
				receiveFile ( fileName );
			} else if (message.equals("JAR RUN")) {
				fileName = ( String ) receive ( );
				receiveFile ( fileName );
			} 

			// pause para asegurar que los archivos se guardan antes de utilizarlos
			// evita que se use la version anterior y produzca resultados equivocados
			
			System.out.println("Ejecutando RTPLauncher.jar en las VMs");
			Util.execute("sh /home/carlos/launcherScript.sh &");
//			#!/bin/bash
//			cd Test
//			java -jar RTPLauncher.jar > results.txt &
						
//			File fileFlag = new File(configuration.getPathFileFlag()+"testFinished.txt");
//			while (true) {
//				if (fileFlag.exists()) {
//					// envía al servidor que la prueba terminó
//					send ("END");
//					Util.pause(1);
//					break; //System.exit(0); 
//				}
//				Util.pause(1);
//			}
		}
		// Puede lanzar una excepción de entrada y salida
		catch ( IOException e )
		{
			e.printStackTrace ( );
		}
		// Puede lanzar una excepción por clase no encontrada.
		catch ( ClassNotFoundException e )
		{
			e.printStackTrace ( );
		}
		// Finalmente se cierran los flujos y el socket.
		finally
		{
			try
			{
				if ( inFromServer != null ) inFromServer.close ( );
				if ( outToServer  != null ) outToServer.close ( );
				if ( clientSocket != null ) clientSocket.close ( );
			}
			catch ( IOException e )
			{
				e.printStackTrace ( );
			}
		}

		System.out.println ( "OK" );
	}

	/**
	 * Este método recibe un archivo enviado por el servidor.
	 * @throws IOException
	 */
	private void receiveFile ( String fileName ) throws IOException
	{
		try
		{
			// Se crea el archivo con el nombre especificado en la carpeta Test.
			// Observe que esta carpeta debe existir en el host el cliente.
			file = new File ( "Test" + File.separator + fileName );
			out = new FileOutputStream ( file );

			// El cliente recibe el número de bloques que compone el archivo.
			int numberOfBlocks = ( ( Integer ) receive ( ) ).intValue ( );

			// Se reciben uno a uno los bloques que conforman el archivo y se
			// almacenan en el archivo.
			for ( int i = 0; i < numberOfBlocks; i++ )
			{
				byte [ ] buffer = ( byte [ ] ) receive ( );



				out.write ( buffer, 0, buffer.length );
			}
		}
		// Puede lanzar una excepción por clase no encontrada.
		catch ( ClassNotFoundException e )
		{
			e.printStackTrace ( );
		}
		// Puede lanzar una excepción por un archivo no encontrado.
		catch ( FileNotFoundException e )
		{
			e.printStackTrace ( );
		}
		// Puede lanzar una excepción de entrada y salida.
		catch ( IOException e )
		{
			e.printStackTrace ( );
		}
		// Finalmente se cierra el archivo.
		finally
		{
			if ( out != null ) out.close ( );
		}
	}

	/**
	 * Este método permite crear los flujos de entrada y salida necesarios para
	 * comunicar el cliente y el servidor.
	 * @throws IOException
	 */
	private void crearFlujos ( ) throws IOException
	{
		// Creación del flujo de salida hacia el servidor.
		outToServer = new ObjectOutputStream ( clientSocket.getOutputStream ( ) );

		// Creación del flujo de entrada desde el servidor.
		inFromServer = new ObjectInputStream ( clientSocket.getInputStream ( ) );
	}

	/**
	 * Este método permite enviar un objeto al servidor.
	 * @param o	Recibe por parámetro el objeto que desea enviar.
	 * @throws IOException
	 */
	private void send ( Object o ) throws IOException
	{
		outToServer.writeObject ( o );
		outToServer.flush ( );
	}

	/**
	 * Este método permite recibir un objeto enviado por el servidor.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object receive ( ) throws IOException, ClassNotFoundException
	{
		return inFromServer.readObject ( );
	}
	
//	private void execute(String command) {
//		Runtime r = Runtime.getRuntime();
//		Process p = null;
//
//		try {
//			p = r.exec(command);
//
//			String line;
//
//			BufferedReader input = new BufferedReader(new InputStreamReader(
//					p.getErrorStream()));
//
//			while ((line = input.readLine()) != null) {
//				System.out.println(line);
//			}
//			input.close();
//		} catch (Exception e) {
//			System.err.println("Error while executing " + command);
//		}
//	}

	/**
	 * Método principal utilizado para lanzar el programa cliente.
	 */
	public static void main ( String args [ ] ) throws Exception
	{
		TestPlayerClient cl = new TestPlayerClient ( );
		cl.init();
	}
}
