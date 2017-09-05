package org.gmagnotta.log;

import java.util.Date;

/**
 * Represents an information that should be logged
 */
public class LogEvent {
	
	private LogLevel logLevel;
	private String sourceClass;
	private Date date;
	private String threadName;
	private String message;
	private Throwable throwable;
	
	/**
	 * Create new log
	 * 
	 * @param logLevel
	 *            log level
	 * @param sourceClass
	 *            source class
	 * @param date
	 *            log date
	 * @param threadName
	 *            the name of thread who generate the log
	 * @param message
	 *            log message
	 */
	public LogEvent(LogLevel logLevel, Class<?> sourceClass, Date date, String threadName, String message) {
		this.logLevel = logLevel;
		this.sourceClass = sourceClass.getName();
		this.date = date;
		this.threadName = threadName;
		this.message = message;
	}

	/**
	 * Create new log
	 *
	 * @param logLevel
	 *            log level
	 * @param sourceClass
	 *            source class
	 * @param date
	 *            log date
	 * @param threadName
	 *            the name of thread who generate the log
	 * @param message
	 *            log message
	 */
	public LogEvent(LogLevel logLevel, String sourceClass, Date date, String threadName, String message) {
		this.logLevel = logLevel;
		this.sourceClass = sourceClass;
		this.date = date;
		this.threadName = threadName;
		this.message = message;
	}

	/**
	 * Create new log
	 * 
	 * @param logLevel
	 *            log level
	 * @param sourceClass
	 *            source class
	 * @param date
	 *            log date
	 * @param threadName
	 *            the name of thread who generate the log
	 * @param message
	 *            log message
	 * @param throwable
	 *            throwable to log
	 */
	public LogEvent(LogLevel logLevel, Class<?> sourceClass, Date date, String threadName, String message,
			Throwable throwable) {
		this.logLevel = logLevel;
		this.sourceClass = sourceClass.getName();
		this.date = date;
		this.threadName = threadName;
		this.message = message;
		this.throwable = throwable;
	}

	/**
	 * Create new log
	 *
	 * @param logLevel
	 *            log level
	 * @param sourceClass
	 *            source class
	 * @param date
	 *            log date
	 * @param threadName
	 *            the name of thread who generate the log
	 * @param message
	 *            log message
	 * @param throwable
	 *            throwable to log
	 */
	public LogEvent(LogLevel logLevel, String sourceClass, Date date, String threadName, String message,
			Throwable throwable) {
		this.logLevel = logLevel;
		this.sourceClass = sourceClass;
		this.date = date;
		this.threadName = threadName;
		this.message = message;
		this.throwable = throwable;
	}

	/**
	 * Get log level
	 * 
	 * @return log level
	 */
	public LogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * Get source class
	 * 
	 * @return source class
	 */
	public String getSourceClass() {
		return sourceClass;
	}

	/**
	 * Get log date
	 * 
	 * @return log date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get name of thread who generate this log
	 * 
	 * @return thread name
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * Get log message
	 * 
	 * @return log message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get throwable to log
	 * 
	 * @return throwable or null if not throwable is defined for this log
	 */
	public Throwable getThrowable() {
		return throwable;
	}

}
