package org.gmagnotta.log;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Spooler class that takes log events from the queue and send them to each logger strategy
 */
public class LogEventSpooler implements Runnable {

	private LinkedBlockingQueue<LogEvent> logEventsQueue;
	private List<LogEventWriter> strategies;

	public LogEventSpooler(LinkedBlockingQueue<LogEvent> logEventsQueue, List<LogEventWriter> strategies) {
		this.logEventsQueue = logEventsQueue;
		this.strategies = strategies;
	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {

			try {

				LogEvent logEvent = logEventsQueue.take();
				
				// Get next logger strategy
				for (LogEventWriter loggerStrategy : strategies) {
					
					// write event logger strategy
					loggerStrategy.write(logEvent);

				}

			} catch (InterruptedException ex) {

				// We were interrupted!
				return;
				
			}

		}

	}

}