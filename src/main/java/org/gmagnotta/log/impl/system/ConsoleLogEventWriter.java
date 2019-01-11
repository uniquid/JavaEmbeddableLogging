package org.gmagnotta.log.impl.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;

/**
 * A logger strategy that redirects all output to System.out
 */
public class ConsoleLogEventWriter implements LogEventWriter {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";

	private String dateFormat;

	public ConsoleLogEventWriter() {
		this(defaultDateFormat);
	}

	public ConsoleLogEventWriter(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public synchronized void write(LogEvent log) {

		// Get log detail
		LogLevel logLevel = log.getLogLevel();
		String sourceClass = log.getSourceClass();
		Date date = log.getDate();
		String threadName = log.getThreadName();
		String message = log.getMessage();
		Throwable throwable = log.getThrowable();

		// Create date format
		DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

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

		System.out.println(buf.toString());

		// Check if throwable is not null
		if (throwable != null) {

			// Log throwable
			throwable.printStackTrace();

		}

	}

	@Override
	public void stop() {
		// NOTHING TO DO
	}

	@Override
	public void setLogName(String logName) {
		// NOTHING TO DO
	}

}
