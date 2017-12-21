package org.gmagnotta.log.impl.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;
import org.gmagnotta.log.LogLevel;

public class MarkerAwareConsoleLogEventWriter implements LogEventWriter {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";

	private String marker;
	private String dateFormat;

	public MarkerAwareConsoleLogEventWriter(String marker) {

		this(defaultDateFormat, marker);

	}

	public MarkerAwareConsoleLogEventWriter(String dateFormat, String marker) {

		this.dateFormat = dateFormat;
		this.marker = marker;
	}

	@Override
	public synchronized void write(LogEvent log) {

		if (log.getMarker().equals(marker)) {

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

	}

	@Override
	public void stop() throws InterruptedException {
		// NOTHING TO DO
	}

}
