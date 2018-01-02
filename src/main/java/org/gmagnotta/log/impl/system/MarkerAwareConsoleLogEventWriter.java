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
			Date date = log.getDate();
			String message = log.getMessage();

			// Create date format
			DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

			StringBuilder buf = new StringBuilder(32);

			// Append date
			buf.append(dateFormatter.format(date));
			buf.append(' ');

			// Append message
			buf.append(message);
			buf.append(' ');

			System.out.println(buf.toString());

		}

	}

	@Override
	public void stop() {
		// NOTHING TO DO
	}

}
