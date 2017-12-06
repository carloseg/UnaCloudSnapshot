package co.edu.uniandes.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerUtil {
	public static Logger getAppLoggers(String classname, String directoryPath, String name) throws IOException {
		Logger rootLogger = Logger.getLogger(classname);
		Layout pattern = new PatternLayout("%d{MM-dd-yyyy HH:mm:ss,SSS} %-5p %c:%m%n");
		
		String filename1 = directoryPath + File.separator + name + "-Debug";
		Appender fileAppender1 = new FileAppender(pattern, filename1, true);
		rootLogger.removeAllAppenders();
		rootLogger.setAdditivity(false);
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(fileAppender1);

		String filename2 = directoryPath + File.separator + name + "-Info";
		Appender fileAppender2 = new FileAppender(pattern, filename2, true);
		rootLogger.setLevel(Level.INFO);
		rootLogger.addAppender(fileAppender2);
		
		Appender consoleAppender = new ConsoleAppender(pattern);
		rootLogger.addAppender(consoleAppender);
				
		return rootLogger;
	}

	public static Logger getAppLoggerDebug(String classname, String directoryPath, String name) throws IOException {
		Logger rootLogger = Logger.getLogger(classname);
		Layout pattern = new PatternLayout("%d{MM-dd-yyyy HH:mm:ss,SSS} %-5p %c:%m%n");
		String filename = directoryPath + File.separator + name + "-Debug";
		Appender fileAppender = new FileAppender(pattern, filename, true);
		rootLogger.removeAllAppenders();
		rootLogger.setAdditivity(false);
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(fileAppender);
		
		Appender consoleAppender = new ConsoleAppender(pattern);
		rootLogger.addAppender(consoleAppender);
				
		return rootLogger;
	}

	public static Logger getAppLoggerInfo(String classname, String directoryPath, String name) throws IOException {
		Logger rootLogger = Logger.getLogger(classname);
		Layout pattern = new PatternLayout("%d{MM-dd-yyyy HH:mm:ss,SSS} %-5p %c:%m%n");
		String filename = directoryPath + File.separator + name + "-Info";
		Appender fileAppender = new FileAppender(pattern, filename, true);
		rootLogger.removeAllAppenders();
		rootLogger.setAdditivity(false);
		rootLogger.setLevel(Level.INFO);
		rootLogger.addAppender(fileAppender);
		
		Appender consoleAppender = new ConsoleAppender(pattern);
		rootLogger.addAppender(consoleAppender);
				
		return rootLogger;
	}
}
