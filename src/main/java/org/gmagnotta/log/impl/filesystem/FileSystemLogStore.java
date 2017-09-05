package org.gmagnotta.log.impl.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The standard file system log store implementation
 */
public class FileSystemLogStore {

	private int maxFileSize;
	private int maxFileCount;

	private File logFolder;
	
	/**
	 * Create new file system log store
	 * 
	 * @param maxFileSize
	 *            the max file size
	 * @param maxFileCount
	 *            the max file count
	 * @param logFolder
	 *            the log files folder
	 */
	public FileSystemLogStore(int maxFileSize, int maxFileCount, File logFolder) {

		this.maxFileSize = maxFileSize;
		this.maxFileCount = maxFileCount;
		this.logFolder = logFolder;

	}

	/**
	 * Get list of log file name order from the newer to the older.
	 * 
	 * @return a list of log file names as {@link String}
	 * @throws IOException
	 *             if an error occurs getting log file names
	 */
	public List<String> getOrderedLogFileNames() throws IOException {

		// Get log file names
		List<String> logFileNames = getLogFileNames();

		// Order log file names
		Collections.sort(logFileNames, new LogFileNameComparator());

		return logFileNames;

	}

	/**
	 * Get the active log file name, means the log file name that can be used in
	 * order to append logs.<br>
	 * Based on log file size limit and log file count limit this method will
	 * create, store and delete log files.
	 * 
	 * @return the active log file name
	 * @throws IOException
	 *             if an error occurs getting active file name
	 */
	public String getActiveLogFileName() throws IOException {

		// Get orderer log file names
		List<String> logFileNames = getOrderedLogFileNames();

		// Get log file name count
		int logFileNameCount = logFileNames.size();

		if (logFileNameCount == 0) {

			// Create new log file
			String newLogFileName = createNewLogFile();

			return newLogFileName;

		} else {

			// Get last log file name
			String lastLogFileName = (String) logFileNames.get(0);

			// Get last log file size
			long lastLogFileSize = getLogFileSize(lastLogFileName);

			if (lastLogFileSize > maxFileSize) {

				// Create new log file
				String newLogFileName = createNewLogFile();

				return newLogFileName;

			} else {

				return lastLogFileName;

			}

		}

	}

	/**
	 * Create a new log file which name is base on current time
	 * 
	 * @return the new log file name
	 * @throws IOException
	 *             if an error occurs creating new log file
	 */
	private String createNewLogFile() throws IOException {

		// Create log file name
		String logFileName = LogFileNameUtil.getLogFileName(new Date());

		// Create new log file
		createLogFile(logFileName);

		// Get log file names
		List<String> logFileNames = getOrderedLogFileNames();

		// Get log file name count
		int logFileNameCount = logFileNames.size();

		if (logFileNameCount > maxFileCount) {

			// Get log file names to be deleted
			List<String> logFileNamesToBeDeleted = logFileNames.subList(maxFileCount,
					logFileNameCount);

			// Archive and delete log files
			archiveAndDeleteLogFiles(logFileNamesToBeDeleted);

		}

		return logFileName;

	}

	/**
	 * Archive and delete given log file
	 * 
	 * @param logFileNames
	 *            the list of log file name of file that must be archived and
	 *            deleted as list of {@link String}
	 * @throws IOException
	 *             if an error occurs deleting log file
	 */
	private void archiveAndDeleteLogFiles(List<String> logFileNames) throws IOException {

		for (String logFileName : logFileNames) {

			// Archive log file
			archive(logFileName);

			// Delte log file
			deleteLogFile(logFileName);

		}

	}

	/**
	 * Archive log file with given name
	 * 
	 * @param logFileName
	 *            the log file name to be archived
	 */
	private void archive(String logFileName) {

		// DO NOTHING

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * getLogFileNames()
	 */
	public List<String> getLogFileNames() throws IOException {

		// Get log file names
		String[] logFileNames = logFolder.list(new LogFileFilenameFilter());

		if (logFileNames == null) {
			throw new IOException("an error occurs getting log file names");
		}
		
		// Get as list
		List<String> logFileNameList = Arrays.asList(logFileNames);

		return logFileNameList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * createLogFile(java.lang.String)
	 */
	public void createLogFile(String logFileName) throws IOException {

		// Get log file
		File logFile = new File(logFolder, logFileName);

		// Create log file
		boolean result = logFile.createNewFile();

		if (!result) {

			throw new IOException("an error occurs creating log file: "
					+ logFile.getPath());

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * deleteLogFile(java.lang.String)
	 */
	public void deleteLogFile(String logFileName) throws IOException {

		// Get log file
		File logFile = new File(logFolder, logFileName);

		// Delete file
		boolean result = logFile.delete();

		if (!result) {

			throw new IOException("an error occurs deleting log file: "
					+ logFile.getPath());

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * getLogFileSize(java.lang.String)
	 */
	public long getLogFileSize(String logFileName) throws IOException {

		// Get log file
		File logFile = new File(logFolder, logFileName);

		// Get log file size
		long size = logFile.length();

		return size;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * getLogFileOutputStream(java.lang.String)
	 */
	public OutputStream getLogFileOutputStream(String logFileName)
			throws IOException {

		// Get log file
		File logFile = new File(logFolder, logFileName);

		// Get file output stream
		OutputStream outputStream = new FileOutputStream(logFile, true);

		return outputStream;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.phoenix2.embedded.client.common.logger.impl.filesystem.LogStore#
	 * getLogFileInputStream(java.lang.String)
	 */
	public InputStream getLogFileInputStream(String logFileName)
			throws IOException {

		// Get log file
		File logFile = new File(logFolder, logFileName);

		// Get file input stream
		InputStream inputStream = new FileInputStream(logFile);

		return inputStream;

	}

}
