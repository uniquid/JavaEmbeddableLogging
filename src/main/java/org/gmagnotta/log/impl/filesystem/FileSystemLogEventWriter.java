package org.gmagnotta.log.impl.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;

public class FileSystemLogEventWriter implements LogEventWriter {

	private FileSystemLogStore logStore;
	private Thread thread;
	private FileSystemSpooler fileSystemSpooler;

	public FileSystemLogEventWriter(FileSystemLogStore logStore) {

		this.logStore = logStore;
		this.fileSystemSpooler = new FileSystemSpooler(logStore);

		thread = new Thread(fileSystemSpooler, "FileSystemSpooler");
		thread.start();

	}

	@Override
	public void write(LogEvent log) {

		fileSystemSpooler.add(log);

	}

	@Override
	public void stop() {

		// Interrupt spooler
		thread.interrupt();

	}

	@Override
	public void setLogName(String logName) {
		// NOTHING TO DO
	}

	/**
	 * Export log files data to given output stream
	 * 
	 * @param outputStream
	 *            the target output stream
	 * @throws IOException
	 *             if an error occurs exporting log files data
	 */
	public synchronized void export(OutputStream outputStream) throws IOException {

		// Get log file names
		List<String> logFileNames = logStore.getReversedOrderedLogFileNames();

		for (String logFileName : logFileNames) {

			// Export log file
			export(logFileName, outputStream);

		}

	}
	
	/**
	 * Export data from log file with given name to given output stream
	 * 
	 * @param logFileName
	 *            the log file name
	 * @param outputStream
	 *            the target output stream
	 * @throws IOException
	 *             if an error occurs exporting log file dra
	 */
	private void export(String logFileName, OutputStream outputStream) throws IOException {

		InputStream inputStream = null;

		// Write log file name
		outputStream.write(logFileName.getBytes());

		// Get input stream from log file
		inputStream = logStore.getLogFileInputStream(logFileName);

		// Transfer data
		transfer(inputStream, outputStream, 64 * 1024);

	}
	
	/**
	 * Transfer data from input stream to output stream
	 * 
	 * @param inputStream
	 *            input stream from which read data
	 * @param outputStream
	 *            output stream on which write data
	 * @param bufferSize
	 *            buffer size
	 * @throws IOException
	 *             if an error occurs during transfer
	 */
	private static void transfer(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {

		// Create buffer
		byte[] buffer = new byte[bufferSize];

		int length = -1;

		// Read from input stream
		while ((length = inputStream.read(buffer)) != -1) {

			// Write to output stream
			outputStream.write(buffer, 0, length);

		}

	}

}
