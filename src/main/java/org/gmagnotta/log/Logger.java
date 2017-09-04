package org.gmagnotta.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Logger {
	
	private static final Object SYNCHRONIZE_OBJECT = new Object();

	private static final List<LoggerStrategy> LOGGER_STATEGIES = new ArrayList<LoggerStrategy>();

	private String sourceClass;

	/**
	 * Add given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public static void addLoggerStrategy(LoggerStrategy loggerStrategy) {
		
		synchronized (SYNCHRONIZE_OBJECT) {

			// Add logger strategy
			LOGGER_STATEGIES.add(loggerStrategy);

		}
		
	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public static void removeLoggerStrategy(LoggerStrategy loggerStrategy) {
		
		synchronized (SYNCHRONIZE_OBJECT) {

			// Remove logger strategy
			LOGGER_STATEGIES.remove(loggerStrategy);

		}
		
	}

	/**
	 * Remove all logger strategy
	 */
	public static void removeLoggerStrategies() {
		
		synchronized (SYNCHRONIZE_OBJECT) {
			
			// Remove all logger strategy
			LOGGER_STATEGIES.clear();

		}
		
	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LoggerStrategy}
	 */
	public static List<LoggerStrategy> getLoggerStrategies() {
		
		synchronized (SYNCHRONIZE_OBJECT) {
			
			return LOGGER_STATEGIES;
			
		}
	}

	/**
	 * Stop logger and wait for all logger thread termination
	 * 
	 * @throws InterruptedException
	 *             if thread is interrupted when waiting for termination
	 */
	public static void stop() throws InterruptedException {
		
		synchronized (SYNCHRONIZE_OBJECT) {

			for (Iterator<LoggerStrategy> iterator = LOGGER_STATEGIES.iterator(); iterator.hasNext();) {

				// Get next logger strategy
				LoggerStrategy loggerStrategy = (LoggerStrategy) iterator.next();

				// Stop logger strategy
				loggerStrategy.stop();

			}

		}
		
	}

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
	 * Log as debug level
	 * 
	 * @param message
	 *            message to log
	 */
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
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
	 * Log as warning level
	 * 
	 * @param message
	 *            message to log
	 */
	public void warning(String message) {
		log(LogLevel.WARNING, message);
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
	 * @param throwable
	 *            throwable to log
	 */
	public void error(Throwable throwable) {
		log(LogLevel.ERROR, throwable.getMessage(), throwable);
	}

	/**
	 * Log ass error level
	 * 
	 * @param exception
	 *            exception to log
	 */
	public void error(Exception exception) {

		// Cast to throwable
		Throwable throwable = (Throwable) exception;

		log(LogLevel.ERROR, throwable.getMessage(), throwable);

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
	 *            log message
	 * @param exception
	 *            exception to log
	 */
	public void error(String message, Exception exception) {

		// Cast to throwable
		Throwable throwable = (Throwable) exception;

		log(LogLevel.ERROR, message, throwable);

	}
	
	/**
	 * Log as event level
	 * 
	 * @param message message to log
	 */
	public void event(String message) {
		log(LogLevel.EVENT, message);
	}

	/**
	 * Log the stack trace dump at given log level
	 * 
	 * @param logLevel
	 *            the log level
	 * @param message
	 *            message to log
	 */
	public void dumpStackTrace(LogLevel logLevel, String message) {

		// Create log stack trace throwable for debug purpose
		LogStackTraceThrowable logStackTraceThrowable = new LogStackTraceThrowable(
				message);

		// Log
		log(logLevel, message, logStackTraceThrowable);

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
		LogMessage log = new LogMessage(logLevel, sourceClass, new Date(), threadName, message);

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
		LogMessage log = new LogMessage(logLevel, sourceClass, new Date(), threadName, message, throwable);

		// Call logger connector
		log(log);

	}
	
	/**
	 * Perform log
	 * 
	 * @param log
	 */
	void log(LogMessage log) {

		synchronized (SYNCHRONIZE_OBJECT) {

			for (Iterator<LoggerStrategy> iterator = LOGGER_STATEGIES.iterator(); iterator.hasNext();) {

				// Get next logger strategy
				LoggerStrategy loggerStrategy = (LoggerStrategy) iterator.next();

				// Log
				loggerStrategy.log(log);

			}

		}

	}
	
}
