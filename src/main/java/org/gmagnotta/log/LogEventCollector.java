package org.gmagnotta.log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collects all LogEvent in the system and spools them to registered
 * LogEventWriters
 */
public class LogEventCollector {

	private static LogEventCollector INSTANCE;

	private LogLevel logLevelThreshold;
	private List<LogEventFilter> filters;
	private LogEventSpooler logEventSpooler;
	private Thread writer;

	public static synchronized LogEventCollector getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new LogEventCollector();
			INSTANCE.start();
		}

		return INSTANCE;

	}

	/**
	 * Creates a new instance of this class
	 */
	private LogEventCollector() {

		this.logEventSpooler = new LogEventSpooler();
		this.logLevelThreshold = LogLevel.INFO;
		this.filters = new CopyOnWriteArrayList<LogEventFilter>();

	}

	/**
	 * Add a LogEvent to the queue
	 * 
	 * @param logEvent
	 */
	public void addLogEvent(LogEvent logEvent) {

		// Fetch logLevel from event
		LogLevel logLevel = logEvent.getLogLevel();

		// If level is higher than threshold add to queue
		if (logLevel.isHigherOrEqual(getLogLevelThreshold())) {

			// if filters can accept the log event
			if (canAccept(logEvent)) {

				logEventSpooler.addLogEvent(logEvent);

			}

		}

	}

	/**
	 * Utility method that fails as long as there is a filter that can't accept
	 * the event
	 * 
	 * @param logEvent
	 * @return
	 */
	private boolean canAccept(LogEvent logEvent) {

		boolean canAccept = true;

		for (LogEventFilter filter : filters) {

			if (!filter.accept(logEvent)) {

				canAccept = false;
				break;

			}

		}

		return canAccept;

	}

	/**
	 * Get log level threshold
	 * 
	 * @return log level threshold
	 */
	public synchronized LogLevel getLogLevelThreshold() {

		return logLevelThreshold;

	}

	/**
	 * Set log level threshold
	 * 
	 * @param logLevelThreshold
	 */
	public synchronized void setLogLevelThreshold(LogLevel logLevelThreshold) {

		this.logLevelThreshold = logLevelThreshold;

	}

	/**
	 * Add given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void addLogEventWriter(LogEventWriter logEventWriter) {

		logEventSpooler.addLogEventWriter(logEventWriter);

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void removeLogEventWriter(LogEventWriter logEventWriter) {

		logEventSpooler.removeLogEventWriter(logEventWriter);

	}

	/**
	 * Remove all logger strategy
	 */
	public void clearLogEventWriters() {

		logEventSpooler.clearLogEventWriters();

	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LogEventWriter}
	 */
	public List<LogEventWriter> getLogEventWriters() {

		return logEventSpooler.getLogEventWriters();

	}

	/**
	 * Add given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public synchronized void addLogEventFilter(LogEventFilter logEventFilter) {

		filters.add(logEventFilter);

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public synchronized void removeLogEventFilter(LogEventFilter logEventFilter) {

		filters.remove(logEventFilter);

	}

	/**
	 * Remove all logger strategy
	 */
	public synchronized void clearLogEventFilters() {

		filters.clear();

	}

	/**
	 * Start the LogEventCollector's spooler
	 */
	public synchronized void start() {

		// if writer thread is not alive, create a new thread and start it
		if (writer == null || !writer.isAlive()) {

			writer = new Thread(logEventSpooler, "LogEventSpooler");

			writer.start();

		}

	}

	/**
	 * Stop logger and wait for all logger thread termination
	 * 
	 * @throws InterruptedException
	 *             if thread is interrupted when waiting for termination
	 */
	public synchronized void stop() throws InterruptedException {

		// interrupt writer
		writer.interrupt();

	}

}
