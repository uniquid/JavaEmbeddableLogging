package org.gmagnotta.log;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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

				for (Iterator<LogEventWriter> iterator = strategies.iterator(); iterator.hasNext();) {

					// Get next logger strategy
					LogEventWriter loggerStrategy = (LogEventWriter) iterator.next();

					// Stop logger strategy
					loggerStrategy.write(logEvent);

				}

			} catch (InterruptedException ex) {

				// We were interrupted!
				return;
				
			}

		}

	}

}