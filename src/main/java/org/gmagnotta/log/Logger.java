package org.gmagnotta.log;

import java.util.Date;

/**
 * This class is used to log messages.
 */
public class Logger {
	
	private String sourceClass;
	
	private static final LogEventCollector LOG_EVENT_COLLECTOR = LogEventCollector.getInstance();

	/**
	 * Get logger for given source class
	 * 
	 * @param sourceClass
	 *            source class
	 * @return logger
	 */
	public static Logger getLogger(Class<?> sourceClass) {

		Logger logger = new Logger(sourceClass);

		return logger;

	}

	/**
	 * Get logger for given name class
	 * 
	 * @param name
	 *            name of class
	 * @return logger
	 */
	public static Logger getLogger(String name) {

		Logger logger = new Logger(name);

		return logger;

	}

	/**
	 * Create new logger for given source class
	 * 
	 * @param sourceClass
	 *            source class
	 */
	private Logger(Class<?> sourceClass) {
		this.sourceClass = sourceClass.getName();
	}

	/**
	 * Create new logger for given source class
	 *
	 * @param name
	 *            source class
	 */
	private Logger(String name) {
		this.sourceClass = name;
	}

	/**
	 * Get source class for this logger
	 * 
	 * @return source class
	 */
	public String getSourceClass() {
		return sourceClass;
	}

	/**
	 * Log as trace level
	 * 
	 * @param message message to log
	 */
	public void trace(String message) {
		log(LogLevel.TRACE, message);
	}
	
	/**
	 * Log as trace level attaching a marker
	 * 
	 * @param marker the marker to attach
	 * @param message message to log
	 */
	public void trace(String marker, String message) {
		log(marker, LogLevel.TRACE, message);
	}

	/**
	 * Log as trace level
	 * 
	 * @param message message to log
	 */
	public void trace(String message, Throwable throwable) {
		log(LogLevel.TRACE, message, throwable);
	}
	
	/**
	 * Log as trace level
	 * 
	 * @param message message to log
	 */
	public void trace(String marker, String message, Throwable throwable) {
		log(marker, LogLevel.TRACE, message, throwable);
	}
	
	/**
	 * Log as debug level
	 * 
	 * @param message
	 *            message to log
	 */
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}
	
	/**
	 * Log as debug level
	 * 
	 * @param message
	 *            message to log
	 */
	public void debug(String marker, String message) {
		log(marker, LogLevel.DEBUG, message);
	}
	
	/**
	 * Log as debug level
	 * 
	 * @param message
	 *            message to log
	 */
	public void debug(String message, Throwable throwable) {
		log(LogLevel.DEBUG, message, throwable);
	}
	
	/**
	 * Log as debug level
	 * 
	 * @param message
	 *            message to log
	 */
	public void debug(String marker, String message, Throwable throwable) {
		log(marker, LogLevel.DEBUG, message, throwable);
	}

	/**
	 * Log as info level
	 * 
	 * @param message
	 *            message to log
	 */
	public void info(String message) {
		log(LogLevel.INFO, message);
	}
	
	/**
	 * Log as info level
	 * 
	 * @param message
	 *            message to log
	 */
	public void info(String marker, String message) {
		log(marker, LogLevel.INFO, message);
	}
	
	/**
	 * Log as info level
	 * 
	 * @param message
	 *            message to log
	 */
	public void info(String message, Throwable throwable) {
		log(LogLevel.INFO, message, throwable);
	}
	
	/**
	 * Log as info level
	 * 
	 * @param message
	 *            message to log
	 */
	public void info(String marker, String message, Throwable throwable) {
		log(marker, LogLevel.INFO, message, throwable);
	}

	/**
	 * Log as warning level
	 * 
	 * @param message
	 *            message to log
	 */
	public void warning(String message) {
		log(LogLevel.WARNING, message);
	}
	
	/**
	 * Log as warning level
	 * 
	 * @param message
	 *            message to log
	 */
	public void warning(String marker, String message) {
		log(marker, LogLevel.WARNING, message);
	}
	
	/**
	 * Log as warning level
	 * 
	 * @param message
	 *            message to log
	 */
	public void warning(String message, Throwable throwable) {
		log(LogLevel.WARNING, message, throwable);
	}
	
	/**
	 * Log as warning level
	 * 
	 * @param message
	 *            message to log
	 */
	public void warning(String marker, String message, Throwable throwable) {
		log(marker, LogLevel.WARNING, message, throwable);
	}
	
	/**
	 * Log as error level
	 * 
	 * @param message
	 *            message to log
	 */
	public void error(String message) {
		log(LogLevel.ERROR, message);
	}
	
	/**
	 * Log as error level
	 * 
	 * @param message
	 *            message to log
	 */
	public void error(String marker, String message) {
		log(marker, LogLevel.ERROR, message);
	}

	/**
	 * Log as error level
	 * 
	 * @param message
	 *            message to log
	 * @param throwable
	 *            throwable to log
	 */
	public void error(String message, Throwable throwable) {
		log(LogLevel.ERROR, message, throwable);
	}
	
	/**
	 * Log as error level
	 * 
	 * @param message
	 *            message to log
	 * @param throwable
	 *            throwable to log
	 */
	public void error(String marker, String message, Throwable throwable) {
		log(marker, LogLevel.ERROR, message, throwable);
	}

	/**
	 * Log
	 * 
	 * @param logLevel
	 *            log level
	 * @param message
	 *            log message
	 */
	private void log(LogLevel logLevel, String message) {

		// Get current thread name
		Thread currentThread = Thread.currentThread();

		// Get thread name
		String threadName = currentThread.getName();

		// Create log
		LogEvent log = new LogEvent(logLevel, sourceClass, new Date(), threadName, message);

		// Call logger connector
		log(log);

	}
	
	/**
	 * Log
	 * 
	 * @param marker
	 *            marker string
	 * @param logLevel
	 *            log level
	 * @param message
	 *            log message
	 */
	private void log(String marker, LogLevel logLevel, String message) {

		// Get current thread name
		Thread currentThread = Thread.currentThread();

		// Get thread name
		String threadName = currentThread.getName();

		// Create log
		LogEvent log = new LogEvent(marker, logLevel, sourceClass, new Date(), threadName, message);

		// Call logger connector
		log(log);

	}

	/**
	 * Log
	 * 
	 * @param logLevel
	 *            log level
	 * @param message
	 *            log message
	 * @param throwable
	 *            throwable to log
	 */
	private void log(LogLevel logLevel, String message, Throwable throwable) {

		// Get current thread name
		Thread currentThread = Thread.currentThread();

		// Get thread name
		String threadName = currentThread.getName();

		// Create log
		LogEvent log = new LogEvent(logLevel, sourceClass, new Date(), threadName, message, throwable);

		// Call logger connector
		log(log);

	}
	
	/**
	 * Log
	 *
	 * @param marker
	 *            marker string
	 * @param logLevel
	 *            log level
	 * @param message
	 *            log message
	 * @param throwable
	 *            throwable to log
	 */
	private void log(String marker, LogLevel logLevel, String message, Throwable throwable) {

		// Get current thread name
		Thread currentThread = Thread.currentThread();

		// Get thread name
		String threadName = currentThread.getName();

		// Create log
		LogEvent log = new LogEvent(marker, logLevel, sourceClass, new Date(), threadName, message, throwable);

		// Call logger connector
		log(log);

	}
	
	/**
	 * Perform log
	 * 
	 * @param log
	 */
	private void log(LogEvent log) {

		LOG_EVENT_COLLECTOR.addLogEvent(log);

	}
	
}
