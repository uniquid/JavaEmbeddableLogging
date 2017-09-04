package org.gmagnotta.log.impl.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.LogMessage;
import org.gmagnotta.log.LoggerStrategy;

/**
 * A logger strategy that redirects all output to System.out
 */
public class SystemLoggerStrategy implements LoggerStrategy {
	
	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";
	
	private LogLevel logLevelThreshold;
	private String dateFormat;
	
	public SystemLoggerStrategy(String dateFormat) {
		this(LogLevel.INFO, dateFormat);
	}
	
	public SystemLoggerStrategy() {
		this(LogLevel.INFO, defaultDateFormat);
	}
	
	public SystemLoggerStrategy(LogLevel logLevel) {
		this(logLevel, defaultDateFormat);
	}
	
	public SystemLoggerStrategy(LogLevel logLevel, String dateFormat) {
		this.logLevelThreshold = logLevel;
		this.dateFormat = dateFormat;
	}

	@Override
	public synchronized void log(LogMessage log) {
		
		// Compare log level to threshold
		LogLevel logLevel = log.getLogLevel();
		
		if (logLevel.compareTo(logLevelThreshold) >= 0) {

			// Get log detail
			String sourceClass = log.getSourceClass();
			Date date = log.getDate();
			String threadName = log.getThreadName();
			String message = log.getMessage();
			Throwable throwable = log.getThrowable();
	
			// Create date format
			DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
			
			StringBuilder buf = new StringBuilder(32);
			
			// Append date
			buf.append(dateFormatter.format(date));
			buf.append(' ');
			
			// Append Thread name
			buf.append(threadName);
			buf.append(' ');
			
			// Append Level in brackets
			buf.append('[');
			buf.append(logLevel.toString());
			buf.append(']');
			buf.append(' ');
			
			// Append name
			buf.append(sourceClass);
			buf.append(' ');
			
			// Append message
			buf.append(message);
			buf.append(' ');
			
			System.out.println(buf.toString());
			
			// Check if throwable is not null
			if (throwable != null) {
	
				// Log throwable
				throwable.printStackTrace();
	
			}
		
		}

	}

	@Override
	public void stop() throws InterruptedException {
		// NOTHING TO DO
	}

	@Override
	public LogLevel getLogLevel() {
		return logLevelThreshold;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.logLevelThreshold = logLevel;
	}

}
