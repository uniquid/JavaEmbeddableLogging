package org.gmagnotta.log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Collects all LogEvent in the system
 */
public class LogEventCollector {

	private static LogEventCollector INSTANCE;

	private LinkedBlockingQueue<LogEvent> logEventsQueue;
	private LogLevel logLevelThreshold;
	private List<LogEventWriter> writers;
	private List<LogEventFilter> filters;
	private Thread writer;

	public static synchronized LogEventCollector getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new LogEventCollector();
		}

		return INSTANCE;

	}

	/**
	 * Creates a new instance of this class
	 */
	private LogEventCollector() {

		this.logEventsQueue = new LinkedBlockingQueue<LogEvent>();
		this.logLevelThreshold = LogLevel.INFO;
		this.writers = new CopyOnWriteArrayList<LogEventWriter>();
		this.filters = new CopyOnWriteArrayList<LogEventFilter>();
		this.writer = new Thread(new LogEventSpooler(logEventsQueue, writers), "LogEventSpooler");

		writer.setDaemon(true);
		writer.start();

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

				logEventsQueue.add(logEvent);
			
			}
			
		}

	}
	
	/**
	 * Utility method that fails as long as there is a filter that can't accept the event
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
	public synchronized void addLogEventWriter(LogEventWriter logEventWriter) {

		writers.add(logEventWriter);

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public synchronized void removeLogEventWriter(LogEventWriter logEventWriter) {

		writers.remove(logEventWriter);

	}

	/**
	 * Remove all logger strategy
	 */
	public synchronized void clearLogEventWriters() {

		writers.clear();

	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LogEventWriter}
	 */
	public synchronized List<LogEventWriter> getLogEventWriters() {

		return writers;
		
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
	public synchronized void removeLogEventFilters() {

		filters.clear();

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

		for (LogEventWriter logEventWriter : writers) {

			logEventWriter.stop();

		}

	}
	
	@Override
	protected void finalize() throws Throwable {

		try {

			stop();

		} catch (InterruptedException e) {
			// DO NOTHING
		}

		super.finalize();
		
	}

}
