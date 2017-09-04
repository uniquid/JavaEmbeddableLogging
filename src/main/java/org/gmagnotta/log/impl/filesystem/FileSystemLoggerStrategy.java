package org.gmagnotta.log.impl.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.LogMessage;
import org.gmagnotta.log.LoggerStrategy;

public class FileSystemLoggerStrategy implements LoggerStrategy {

	private static final String defaultDateFormat = "dd/MM/yyyy HH:mm:ss.SSS";

	private LogLevel logLevelThreshold;
	private PrintStream printStream;

	public FileSystemLoggerStrategy(File logfile) throws FileNotFoundException {

		FileOutputStream fos = new FileOutputStream(logfile);
		printStream = new PrintStream(fos);

	}

	@Override
	public LogLevel getLogLevel() {
		return logLevelThreshold;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.logLevelThreshold = logLevel;
	}

	@Override
	public void log(LogMessage log) {

		// Compare log level to threshold
		LogLevel logLevel = log.getLogLevel();

		if (logLevel.compareTo(logLevelThreshold) >= 0) {

			// Get log detail
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

			printStream.println(buf.toString());
			if (throwable != null) {
				throwable.printStackTrace(printStream);
			}
			printStream.flush();

			// Check if throwable is not null
			if (throwable != null) {

				// Log throwable
				throwable.printStackTrace();

			}

		}
	}

	@Override
	public void stop() throws InterruptedException {
		// TODO Auto-generated method stub

	}

}
