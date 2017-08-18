/*
 * El objetivo de este programa es transferir un archivo desde el servidor al
 * cliente. El nombre del archivo es solicitado al usuario para que lo ingrese
 * por teclado. El cliente envía el nombre del archivo solicitado al servidor
 * y el servidor envía el archivo por fragmentos, si se hace necesario. El
 * servidor busca el archivo en la carpeta Shared. El cliente crea el archivo
 * dentro de la carpeta Test.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import co.edu.uniandes.configuration.ServerConfiguration;
import co.edu.uniandes.util.Util;

public class TestPlayerServer
{
	public final static int PORT = 20000;

	// Constante que define el tamaño de un fragmento. Un archivo que mida mas que
	// el tamaño del fragmento, deberá ser enviado por partes.
	public final static int BUFFER_SIZE = 1024;

	private ServerSocket       welcomeSocket;
	private Socket             connectionSocket;
	private ObjectInputStream  inFromClient;
	private ObjectOutputStream outToClient;
	private File               file;
	private ServerConfiguration configuration;

	// Método constructor.
	public TestPlayerServer ( )
	{
	}
	
	public void init ( )
	{
		// reading the configuration properties
		configuration = new ServerConfiguration("server.properties");
		String propertiesFileName = configuration.getProperties();
		String jarFileName = configuration.getJar();

		System.out.println ( "File Transfer Server ..." );

//		RingNameServer rns = new RingNameServer();
		
		// Inicia el ring name server
		String s = Util.execute("/bin/sh /Users/carlos/Documents/enviar/ringNSLauncher.sh > /Users/carlos/Documents/enviar/xx.txt");
//		#!/bin/bash
//		cd /Users/carlos/Documents/enviar/; java -jar RingNameServer.jar > results.txt

		System.out.println(s);
		
		

		
		try
		{
			welcomeSocket = new ServerSocket ( PORT );

			while ( true )
			{
				connectionSocket = welcomeSocket.accept ( );

				crearFlujos ( );

				String message = ( String ) receive ( );
				
				if (message.equals("HELLO")) {
					if (!propertiesFileName.equals("no") && !jarFileName.equals("no")) {
						send ("PROPERTIES JAR RUN");
						send ( propertiesFileName );
						sendFile ( propertiesFileName );
						send ( jarFileName );
						sendFile ( jarFileName );
					} else if (!propertiesFileName.equals("no") && jarFileName.equals("no")) {
						send ("PROPERTIES RUN");
						send ( propertiesFileName );
						sendFile ( propertiesFileName );
					} else if (propertiesFileName.equals("no") && !jarFileName.equals("no")) {
						send ("JAR RUN");
						send ( jarFileName );
						sendFile ( jarFileName );
					} else {
						send ("RUN");
					}
				} else if (message.equals("END")) {
					/// Prueba terminada
					/// Hay que recoger los resultados y terminar el ringNameServer
					
					Util.pause(1);
					
					// consulta los tiempos de la prueba
//					String r = NamesUtil.nameServerRingQueryTime(
//							configuration.getNameServerHostName(),
//							configuration.getNameServerPort());
//					
//					r = r.replace(';', '\n');
//					
//					System.out.println(r);
//
//					// termina el ringNameServer
//					NamesUtil.nameServerRingQuit(
//							configuration.getNameServerHostName(),
//							configuration.getNameServerPort());
				}
			}
		}
		// Puede lanzar una excepción de entrada y salida.
		catch ( IOException e )
		{
			e.printStackTrace ( );
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Al terminar se cierran los sockets y los flujos.
		finally
		{
			try
			{
				if ( outToClient      != null ) outToClient.close ( );
				if ( inFromClient     != null ) inFromClient.close ( );
				if ( connectionSocket != null ) connectionSocket.close ( );
				if ( welcomeSocket    != null )	welcomeSocket.close ( );
			}
			// Puede lanzar una excepción de entrada y salida.
			catch ( IOException e )
			{
				e.printStackTrace ( );
			}
		}
	}

	/**
	 * Este metodo envía un archivo al cliente.
	 * @param filename Es el nombre del archivo solicitado a enviar.
	 * @throws IOException
	 */
	private void sendFile ( String fileName ) throws IOException
	{
		FileInputStream fileIn = null;

		try
		{
			// Los archivos compartidos se almacenan en la carpeta Shared. Observe
			// que esta carpeta debe existir en el host el servidor y que en ella
			// debe existir el archivo a enviar.
			file = new File ( "Shared" + File.separator + fileName );

			// Abre el archivo solicitado.
			fileIn = new FileInputStream ( file );

			// Se obtiene el tamaño del archivo y se imprime en la consola.
			long size = file.length ( );
			System.out.println ( "Size: " + size );

			// Se calcula el número de bloques y el tamaño del ultimo bloque.
			int numberOfBlocks  = ( int ) ( size / BUFFER_SIZE );
			int sizeOfLastBlock = ( int ) ( size % BUFFER_SIZE );

			// Si el archivo no se puede partir en bloques de igual tamaño queda un
			// bloque adicional, más pequeño.
			if ( sizeOfLastBlock > 0 )
			{
				numberOfBlocks++;
			}

			// Se imprimen en la consola el número de bloques y el tamaño del último
			// bloque.
			System.out.println ( "Number of blocks: " + numberOfBlocks );
			System.out.println ( "Size of last block: " + sizeOfLastBlock );

			// Se envía el número de bloques al cliente.
			send ( new Integer ( numberOfBlocks  ) );

			// Si todos los bloques son de igual tamaño, no hay un bloque al final,
			// más pequeño.
			if ( sizeOfLastBlock == 0 )
			{
				// Se envían todos los bloques.
				for ( int i = 0; i < numberOfBlocks; i++ )
				{
					byte [ ] buffer = new byte [ BUFFER_SIZE ];
					fileIn.read ( buffer );


					send ( buffer );
				}
			}
			else
			{
				// Si queda un bloque más pequeño al final, se envían todos los bloques
				// excepto el último.
				for ( int i = 0; i < numberOfBlocks - 1; i++ )
				{
					byte [ ] buffer = new byte [ BUFFER_SIZE ];
					fileIn.read ( buffer );
					send ( buffer );
				}

				// El bloque restante se envía a continuación.
				byte [ ] lastBuffer = new byte [ sizeOfLastBlock ];
				fileIn.read ( lastBuffer);
				send ( lastBuffer );
			}
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
		finally
		{
			if ( fileName != null ) fileIn.close ( );
		}
	}

	/**
	 * Este método permite crear los flujos de entrada y salida necesarios para
	 * comunicar el cliente y el servidor.
	 * @throws IOException
	 */
	private void crearFlujos ( ) throws IOException
	{
		// Creación del flujo de salida hacia el cliente.
		outToClient = new ObjectOutputStream
			( connectionSocket.getOutputStream ( ) );

		// Creación del flujo de entrada desde el cliente.
		inFromClient = new ObjectInputStream
				( connectionSocket.getInputStream ( ) );
	}

	/**
	 * Este método permite enviar un objeto al cliente.
	 * @param o	Recibe por parámetro el objeto que desea enviar.
	 * @throws IOException
	 */
	private void send ( Object o ) throws IOException
	{
		outToClient.writeObject ( o );
		outToClient.flush ( );
	}

	/**
	 * Este método permite recibir un objeto enviado por el cliente.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object receive ( ) throws IOException, ClassNotFoundException
	{
		return inFromClient.readObject ( );
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
	 * Método principal utilizado para lanzar el programa servidor.
	 */
	public static void main ( String args [ ] ) throws Exception
	{
		TestPlayerServer server = new TestPlayerServer ( );
		server.init();

	}
}
