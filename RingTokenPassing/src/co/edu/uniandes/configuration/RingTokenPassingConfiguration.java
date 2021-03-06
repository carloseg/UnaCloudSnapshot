package co.edu.uniandes.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class reads the properties that are required in the ring token pasing
 * application, which can be changed by user.
 *
* @author Carlos Eduardo G�mez Montoya
* @author Jose Gabriel Tamura Lara
* @author Harold Enrique Castro Barrera
*
* 2017
*/

public class RingTokenPassingConfiguration {
	private String nameServerHostName;
	private int nameServerPort;
	
	private String labelLogFile;
	private String pathLog;
	private String logFileName;
	private String logType;
	private String cleanLog;

	/**
	 * This is the constructor.
	 * 
	 * Loads the configurations.
	 * @param String The properties filename.
	 */
	public RingTokenPassingConfiguration(String filename) {
		Properties p = new Properties();
		InputStream is = null;

		try {
			is = new FileInputStream(filename);

			p.load(is);

			nameServerHostName = p.getProperty("testManagerHostName");
			nameServerPort = Integer.parseInt(p.getProperty("testManagerPort"));
			labelLogFile = p.getProperty("labelLogFile");
			pathLog = p.getProperty("pathLog");
			logFileName = p.getProperty("logFileName");
			logType = p.getProperty("logType");
			cleanLog = p.getProperty("cleanLog");

		} catch (IOException e) {
		}
	}

	/**
	 * This method returns the hostname prefix.
	 * 
	 * @return String The hostname prefix.
	 */
	public String getNameServerHostName() {
		return nameServerHostName;
	}

	/**
	 * This method returns the port number of the name server.
	 * 
	 * @return int The port number.
	 */
	public int getNameServerPort() {
		return nameServerPort;
	}


	/**
	 * This method returns the label of each entry in the log file.
	 * 
	 * @return String The label.
	 */
	public String getLabelLogFile() {
		return labelLogFile;
	}

	/**
	 * This method returns the directory path where the log file will be stored.
	 * 
	 * @return String The pathLog.
	 */
	public String getPathLog() {
		return pathLog;
	}

	/**
	 * This method returns the log file name.
	 * 
	 * @return String The log file name.
	 */
	public String getLogFileName() {
		return logFileName;
	}
	
	/**
	 * This method returns the log type.
	 * 
	 * @return String The type of the log file.
	 */
	public String getLogType() {
		return logType;
	}

	/**
	 * This method returns the cleanLog value.
	 * 
	 * @return String The value of the cleanLog property.
	 */
	public String getCleanLog() {
		return cleanLog;
	}
}
