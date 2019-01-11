package org.gmagnotta.log;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Spooler class that takes log events from the queue and send them to each
 * logger strategy
 */
public class LogEventSpooler implements Runnable {

	private BlockingQueue<LogEvent> logEventsQueue;
	private List<LogEventWriter> writers;
	private final Object syncObject;

	public LogEventSpooler() {

		this.logEventsQueue = new LinkedBlockingQueue<>();
		this.writers = new CopyOnWriteArrayList<>();
		this.syncObject = new Object();

	}

	/**
	 * Add a LogEvent to the queue
	 * 
	 * @param logEvent
	 */
	public void addLogEvent(LogEvent logEvent) {

		logEventsQueue.add(logEvent);

	}

	/**
	 * Add given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void addLogEventWriter(LogEventWriter logEventWriter) {

		writers.add(logEventWriter);

		synchronized (syncObject) {

			syncObject.notify();

		}

	}

	/**
	 * Remove given logger strategy
	 * 
	 * @param logEventWriter
	 *            logger strategy
	 */
	public void removeLogEventWriter(LogEventWriter logEventWriter) {

		writers.remove(logEventWriter);

		synchronized (syncObject) {

			syncObject.notify();

		}

	}

	/**
	 * Remove all logger strategy
	 */
	public void clearLogEventWriters() {

		writers.clear();

		synchronized (syncObject) {

			syncObject.notify();

		}

	}

	/**
	 * Get all logger strategies
	 * 
	 * @return list of {@link LogEventWriter}
	 */
	public List<LogEventWriter> getLogEventWriters() {

		return writers;

	}

	/**
	 * Set log name to all writers
	 * @param logName
	 */
	public void setLogName(String logName) {
		for (LogEventWriter writer : writers) {
			writer.setLogName(logName);
		}
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {

			try {

				// This prevents to loose logEvent if there is no writer listening
				synchronized (syncObject) {

					while (writers.isEmpty()) {

						syncObject.wait();

					}

				}

				LogEvent logEvent = logEventsQueue.take();

				// Get next logger strategy
				for (LogEventWriter loggerStrategy : writers) {

					// write event logger strategy
					loggerStrategy.write(logEvent);

					// if we are interrupted we avoid to cycle to all
					// strategies...
					if (Thread.currentThread().isInterrupted()) {

						throw new InterruptedException();

					}

				}

			} catch (InterruptedException ex) {

				// We were interrupted!
				break;

			}

		}

	}

}