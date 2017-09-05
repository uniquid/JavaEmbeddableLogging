package org.gmagnotta.log.impl.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;
import org.gmagnotta.log.LogLevel;

import sun.rmi.runtime.Log;

public class FileSystemLoggerStrategy implements LogEventWriter {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";

	private FileSystemLogStore logStore;

	public FileSystemLoggerStrategy(FileSystemLogStore logStore) throws FileNotFoundException {

		this.logStore = logStore;

	}

	@Override
	public void write(LogEvent log) {

		try {

			// Get active log file name
			String activeLogFileName = logStore.getActiveLogFileName();

			OutputStream outputStream = null;

			try {

				// Get log file output stream
				outputStream = logStore.getLogFileOutputStream(activeLogFileName);

				// Create print writer
				PrintWriter printWriter = new PrintWriter(outputStream, true);

				// Write log
				write(log, printWriter);

			} catch (IOException ex) {

				// TODO

			} finally {

				outputStream.close();

			}

		} catch (Exception ex) {

			// TODO

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

	@Override
	public void stop() throws InterruptedException {
		// TODO Auto-generated method stub

	}

}
