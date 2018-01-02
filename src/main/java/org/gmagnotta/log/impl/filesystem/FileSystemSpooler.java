package org.gmagnotta.log.impl.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogLevel;

import sun.rmi.runtime.Log;

public class FileSystemSpooler implements Runnable {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";
	private static final int MAX_ELEMENTS_TO_SPOOL = 100;

	private FileSystemLogStore logStore;
	private LinkedList<LogEvent> logEventsQueue;

	public FileSystemSpooler(FileSystemLogStore logStore) {

		this.logStore = logStore;
		this.logEventsQueue = new LinkedList<LogEvent>();

	}

	/**
	 * Add logEvent
	 * 
	 * @param logEvent
	 */
	public void add(LogEvent logEvent) {

		synchronized (logEventsQueue) {

			logEventsQueue.add(logEvent);
			logEventsQueue.notify();

		}

	}

	@Override
	public void run() {

		while (!Thread.currentThread().isInterrupted()) {

			try {

				ArrayList<LogEvent> elements = new ArrayList<LogEvent>();

				synchronized (logEventsQueue) {

					while (logEventsQueue.size() == 0) {

						logEventsQueue.wait();

					}

					// Fetch max 100 element
					int count = Math.min(logEventsQueue.size(), MAX_ELEMENTS_TO_SPOOL);

					while (count-- > 0) {

						LogEvent event = logEventsQueue.removeFirst();

						elements.add(event);

					}

				}
				
				// We don't check if we were terminated here. We flush what we read before

				try {

					// Get active log file name
					String activeLogFileName = logStore.getActiveLogFileName();

					OutputStream outputStream = null;

					try {

						// Get log file output stream
						outputStream = logStore.getLogFileOutputStream(activeLogFileName);

						// Create print writer
						PrintWriter printWriter = new PrintWriter(outputStream, true);

						for (LogEvent log : elements) {

							// Write log
							write(log, printWriter);

						}

					} catch (IOException ex) {

						// TODO

					} finally {

						outputStream.close();

					}

				} catch (Exception ex) {

					// TODO

				}

			} catch (InterruptedException ex) {

				break;

			}

		}

	}

	/**
	 * Write given {@link Log} to given {@link PrintWriter}
	 * 
	 * @param log
	 *            the log to be wrote
	 * @param printWriter
	 *            the target print writer
	 * @throws IOException
	 *             if an error occurs writing log
	 */
	private void write(LogEvent log, PrintWriter printWriter) throws IOException {

		// Get log detail
		LogLevel logLevel = log.getLogLevel();
		String sourceClass = log.getSourceClass();
		Date date = log.getDate();
		String threadName = log.getThreadName();
		String message = log.getMessage();
		Throwable throwable = log.getThrowable();

		// Create date format
		DateFormat dateFormatter = new SimpleDateFormat(defaultDateFormat);

		StringBuilder buf = new StringBuilder(32);

		// Append date
		buf.append(dateFormatter.format(date));
		buf.append(' ');

		// Append Thread name
		buf.append(threadName);
		buf.append(' ');

		// Append Level in brackets
		buf.append('[');
		buf.append(logLevel.toString());
		buf.append(']');
		buf.append(' ');

		// Append name
		buf.append(sourceClass);
		buf.append(' ');

		// Append message
		buf.append(message);
		buf.append(' ');

		printWriter.println(buf.toString());
		if (throwable != null) {
			throwable.printStackTrace(printWriter);
		}

		printWriter.flush();

	}

}
