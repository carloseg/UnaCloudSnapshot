package co.edu.uniandes.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class reads the properties that are required in the 
 * application, which can be changed by user.
 *
 * @author Carlos Eduardo Gomez Montoya
 * 
 * 2017
 */

public class Configuration {
//	private String myIP;
	private int base;
	private int offset;
	private String processHostnamePrefix;
	private String nameServerHostName;
	private int nameServerPort;	
	private String vmname;
	private String virtualBoxHome;
	private String virtualMachinesHome;
	private String snapshotNamePrefix;
	private int max;
	private int pause;
	private String labelLogFile;
	private String pathLog;
	private String logFileName;
	private String logType;

	/**
	 * This is the constructor.
	 * 
	 * 
	 * @param String
	 *            The properties filename.
	 */
	public Configuration(String filename) {
		Properties p = new Properties();
		InputStream is = null;

		try {
			is = new FileInputStream(filename);

			p.load(is);
//			myIP = p.getProperty("myIP");
			base = Integer.parseInt(p.getProperty("base"));
			offset = Integer.parseInt(p.getProperty("offset"));
			processHostnamePrefix = p.getProperty("processHostnamePrefix");
			nameServerHostName = p.getProperty("nameServerHostName");
			nameServerPort = Integer.parseInt(p.getProperty("nameServerPort"));
			vmname = p.getProperty("vmname");
			virtualBoxHome = p.getProperty("virtualBoxHome");
			virtualMachinesHome = p.getProperty("virtualMachinesHome");
			snapshotNamePrefix = p.getProperty("snapshotNamePrefix");
			max = Integer.parseInt(p.getProperty("max"));
			pause = Integer.parseInt(p.getProperty("pause"));
			labelLogFile = p.getProperty("labelLogFile");
			pathLog = p.getProperty("pathLog");
			logFileName = p.getProperty("logFileName");
			logType = p.getProperty("logType");

		} catch (IOException e) {
		}
	}

	/**
	 * This method returns the ip address of the process.
	 * 
	 * @return String The ip address of the physical machine.
	 */
//	public String getMyIP() {
//		return myIP;
//	}

	/**
	 * This method returns the base port number.
	 * 
	 * @return int The base port number.
	 */
	public int getBase() {
		return base;
	}

	/**
	 * This method returns the offset to set the minimum value of the VM in a host.
	 * 
	 * @return int The offset.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * This method returns the prefix of the process name.
	 * 
	 * @return int The prefix.
	 */
	public String getProcessHostNamePrefix() {
		return processHostnamePrefix;
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
	 * This method returns the name of the virtual machine.
	 * 
	 * @return String The name of the virtual machine.
	 */
	public String getVmname() {
		return vmname;
	}

	/**
	 * This method returns the directory path where VirtualBox installed is.
	 * 
	 * @return String The directory path of VirtualBox.
	 */
	public String getVirtualBoxHome() {
		return virtualBoxHome;
	}

	/**
	 * This method returns the directory path where the virtual machines stored are.
	 * 
	 * @return String The directory path of the virtual machines repository.
	 */
	public String getVirtualMachinesHome() {
		return virtualMachinesHome;
	}

	/**
	 * This method returns the snapshot name prefix.
	 * 
	 * @return String The snapshot name prefix.
	 */
	public String getSnapshotNamePrefix() {
		return snapshotNamePrefix;
	}

	/**
	 * This method returns the maximum amount of processes in the system.
	 * 
	 * @return int The maximum amount of processes.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * This method returns the amount of miliseconds to wait during different parts of 
	 *  the execution to the algorithm in order to watch it slowly.
	 * 
	 * @return int The amount of miliseconds.
	 */
	public int getPause() {
		return pause;
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
}
