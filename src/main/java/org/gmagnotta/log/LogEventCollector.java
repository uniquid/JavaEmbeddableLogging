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
	private List<LogEventWriter> strategies;
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
		this.strategies = new CopyOnWriteArrayList<LogEventWriter>();
		this.writer = new Thread(new LogEventSpooler(logEventsQueue, strategies));

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

			logEventsQueue.add(logEvent);

		}

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
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public void addLoggerStrategy(LogEventWriter loggerStrategy) {

		synchronized (SYNC_OBJ) {

			// Add logger strategy
			strategies.add(loggerStrategy);

		}

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param loggerStrategy
	 *            logger strategy
	 */
	public void removeLoggerStrategy(LogEventWriter loggerStrategy) {

		synchronized (SYNC_OBJ) {

			// Remove logger strategy
			strategies.remove(loggerStrategy);

		}

	}

	/**
	 * Remove all logger strategy
	 */
	public void removeLoggerStrategies() {

		synchronized (SYNC_OBJ) {

			// Remove all logger strategy
			strategies.clear();

		}

	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LogEventWriter}
	 */
	public List<LogEventWriter> getLoggerStrategies() {

		synchronized (SYNC_OBJ) {

			return strategies;

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

			for (LogEventWriter loggerStrategy : strategies) {

				// Stop logger strategy
				loggerStrategy.stop();

			}

		}

	}

}
