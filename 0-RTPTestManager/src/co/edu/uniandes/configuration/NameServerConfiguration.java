package co.edu.uniandes.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class reads the properties that are required in the name server
 * application, which can be changed by user.
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

public class NameServerConfiguration {
	private int base;
	private String processHostnamePrefix;
	private String nameServerHostName;
	private int nameServerPort;
	private String labelLogFile;
	private String pathLog;
	private String logFileName;
	private String logType;
	
	private int maxTokenValue;
	private int maxBenchmarkValue;
	private int pauseToStartRing;
	private int pauseBenchmark;
	/**
	 * This is the constructor.
	 * 
	 * 
	 * @param String
	 *            The properties filename.
	 */
	public NameServerConfiguration(String filename) {
		Properties p = new Properties();
		InputStream is = null;

		try {
			is = new FileInputStream(filename);

			p.load(is);

			base = Integer.parseInt(p.getProperty("base"));
			processHostnamePrefix = p.getProperty("processHostnamePrefix");
			nameServerPort = Integer.parseInt(p.getProperty("nameServerPort"));
			labelLogFile = p.getProperty("labelLogFile");
			pathLog = p.getProperty("pathLog");
			logFileName = p.getProperty("logFileName");
			logType = p.getProperty("logType");
			
			maxTokenValue = Integer.parseInt(p.getProperty("maxTokenValue"));
			maxBenchmarkValue =  Integer.parseInt(p.getProperty("maxBenchmarckValue"));
			pauseToStartRing  =  Integer.parseInt(p.getProperty("pauseToStartRing"));
			pauseBenchmark  =  Integer.parseInt(p.getProperty("pauseBenchmark"));

		} catch (IOException e) {
		}
	}

	/**
	 * This method returns the base port number.
	 * 
	 * @return int The base port number.
	 */
	public int getBasePort() {
		return base;
	}

	/**
	 * This method returns the name server hostname.
	 * 
	 * @return String The hostname prefix.
	 */
	public String getNameServerHostName() {
		return nameServerHostName;
	}

	/**
	 * This method returns the hostname prefix.
	 * 
	 * @return String The hostname prefix.
	 */
	public String getProcessHostnamePrefix() {
		return processHostnamePrefix;
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
	 * This method returns the max token value.
	 * 
	 * @return int The type of the max token value.
	 */
	public int getMaxTokenValue() {
		return maxTokenValue;
	}
	/**
	 * This method returns the max benchmark value.
	 * 
	 * @return int The type of the max bechmark value.
	 */
	public int getMaxBenchmarkValue() {
		return maxBenchmarkValue;
	}
	/**
	 * This method returns the pause to start the ring
	 * 
	 * @return int 
	 */
	public int getPauseToStartRing() {
		return pauseToStartRing;
	}
	/**
	 * This method returns the pause of the benchmark value.
	 * 
	 * @return int The type of the pause of the bechmark value.
	 */
	public int getPauseBenchmark() {
		return pauseBenchmark;
	}


}
