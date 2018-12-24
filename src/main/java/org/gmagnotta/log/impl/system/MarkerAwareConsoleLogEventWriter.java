package org.gmagnotta.log.impl.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;
import org.gmagnotta.log.LogLevel;

public class MarkerAwareConsoleLogEventWriter implements LogEventWriter {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";

	private List<String> markers;
	private String dateFormat;

	public MarkerAwareConsoleLogEventWriter(String marker) {

		this(defaultDateFormat, toListString(marker));

	}
	
	public MarkerAwareConsoleLogEventWriter(List<String> markers) {

		this(defaultDateFormat, markers);

	}

	public MarkerAwareConsoleLogEventWriter(String dateFormat, List<String> markers) {

		this.dateFormat = dateFormat;
		this.markers = markers;
	}

	@Override
	public synchronized void write(LogEvent log) {

		if (markers.contains(log.getMarker()) &&
				log.getLogLevel().isHigherOrEqual(LogLevel.INFO)) {

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

	@Override
	public void setLogName(String logName) {
		// NOTHING TO DO
	}

	private static List<String> toListString(String string) {
		
		List<String> list = new ArrayList<>();
		list.add(string);
		
		return list;
		
	}

}
