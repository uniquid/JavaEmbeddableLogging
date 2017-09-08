package org.gmagnotta.log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Collects all LogEvent in the system
 */
public class LogEventCollector {

	private static final Object SYNC_OBJ = new Object();

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

	private LogEventCollector() {

		this.logEventsQueue = new LinkedBlockingQueue<LogEvent>();
		this.logLevelThreshold = LogLevel.INFO;
		this.writers = new CopyOnWriteArrayList<LogEventWriter>();
		this.filters = new CopyOnWriteArrayList<LogEventFilter>();
		this.writer = new Thread(new LogEventSpooler(logEventsQueue, writers));

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

		// If level is bigger than threshold add to queue
		if (logLevel.compareTo(getLogLevelThreshold()) >= 0) {
			
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
	public LogLevel getLogLevelThreshold() {

		synchronized (SYNC_OBJ) {

			return logLevelThreshold;

		}

	}

	/**
	 * Set log level threshold
	 * 
	 * @param logLevelThreshold
	 */
	public void setLogLevelThreshold(LogLevel logLevelThreshold) {

		synchronized (SYNC_OBJ) {

			this.logLevelThreshold = logLevelThreshold;

		}

	}

	/**
	 * Add given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void addLogEventWriter(LogEventWriter logEventWriter) {

		synchronized (SYNC_OBJ) {

			writers.add(logEventWriter);

		}

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void removeLogEventWriter(LogEventWriter logEventWriter) {

		synchronized (SYNC_OBJ) {

			writers.remove(logEventWriter);

		}

	}

	/**
	 * Remove all logger strategy
	 */
	public void removeLogEventWriters() {

		synchronized (SYNC_OBJ) {

			writers.clear();

		}

	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LogEventWriter}
	 */
	public List<LogEventWriter> getLogEventWriters() {

		synchronized (SYNC_OBJ) {

			return writers;

		}
	}

	/**
	 * Add given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public void addLogEventFilter(LogEventFilter logEventFilter) {

		synchronized (SYNC_OBJ) {

			filters.add(logEventFilter);

		}

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public void removeLogEventFilter(LogEventFilter logEventFilter) {

		synchronized (SYNC_OBJ) {

			filters.remove(logEventFilter);

		}

	}

	/**
	 * Remove all logger strategy
	 */
	public void removeLogEventFilters() {

		synchronized (SYNC_OBJ) {

			filters.clear();

		}

	}

	/**
	 * Stop logger and wait for all logger thread termination
	 * 
	 * @throws InterruptedException
	 *             if thread is interrupted when waiting for termination
	 */
	public void stop() throws InterruptedException {

		synchronized (SYNC_OBJ) {
			
			// interrupt writer
			writer.interrupt();

			for (LogEventWriter logEventWriter : writers) {

				logEventWriter.stop();

			}

		}

	}

}
