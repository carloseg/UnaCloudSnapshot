package co.edu.uniandes.main;

/**
 * This class resets the name server. 
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */


import co.edu.uniandes.configuration.Configuration;
import co.edu.uniandes.util.NamesUtil;

public class QueryTime {	
	private Configuration configuration;
	
	/**
	 * This is the constructor
	 */
	public QueryTime() {
		// reading the configuration properties
		configuration = new Configuration ("snapshot.properties");
	}
	
	/**
	 * This method initiates the execution of the application
	 */
	public void init(int n) {
		String r = NamesUtil.nameServerQueryTime(
				configuration.getNameServerHostName(),
				configuration.getNameServerPort(), n);
		
		r = r.replace(';', '\n');
		
		System.out.println(r);
	}
	
	/**
	 * This is the main method to launch the application.
	 */
	public static void main(String[] args) {
		QueryTime qt = new QueryTime();
		qt.init(1);
		qt.init(2);
		qt.init(3);
	}
}
