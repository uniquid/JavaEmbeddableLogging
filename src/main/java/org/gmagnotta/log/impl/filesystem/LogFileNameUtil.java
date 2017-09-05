package org.gmagnotta.log.impl.filesystem;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The log file name util
 * 
 */
public class LogFileNameUtil {

	/**
	 * The log file name date pattern
	 */
	private static final String DATE_PATTERN = "yyyy-MM-dd_HH-mm-ss-SSS";

	/**
	 * The log file extension
	 */
	private static final String LOG_FILE_EXTENSION = "log";

	/**
	 * Check if given log file name is valid. In order to be valid a log file
	 * must have:
	 * <ul>
	 * <li><i>.log</i> extensions;</li>
	 * <li>a base name that match <i>yyyy-MM-dd_HH-mm-ss-SSS</i> date pattern.</li>
	 * </ul>
	 * 
	 * @param logFileName
	 *            the log file name to be validated
	 * @return true if given log file name is valid, false otherwise
	 */
	public static final boolean isValid(String logFileName) {

		// Get log file extension
		String logFileExtension = getExtension(logFileName);

		if (!LOG_FILE_EXTENSION.equals(logFileExtension)) {
			return false;
		}

		try {

			// Try to parse log file name
			getLogFileDate(logFileName);

			return true;

		} catch (ParseException ex) {
			return false;
		}

	}

	/**
	 * Get log file date from given log file name
	 * 
	 * @param logFileName
	 *            the log file name
	 * @return the log date
	 * @throws ParseException
	 *             if an error occurs parsing log file name
	 */
	public static final Date getLogFileDate(String logFileName)
			throws ParseException {

		// Get log file base name
		String logFileBaseName = getBasename(logFileName);

		// Create simple date format
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

		// Parse log file base name
		Date logFileDate = simpleDateFormat.parse(logFileBaseName);

		return logFileDate;

	}

	/**
	 * Get log file name from given log file date
	 * 
	 * @param logFileDate
	 *            the log file date
	 * @return the log file name
	 */
	public static final String getLogFileName(Date logFileDate) {

		// Create simple date format
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

		// Format log file base name
		String logFileBaseName = simpleDateFormat.format(logFileDate);

		// Get log file name
		String logFileName = logFileBaseName.concat(".").concat(
				LOG_FILE_EXTENSION);

		return logFileName;

	}
	
	/**
	 * Get file extension
	 * 
	 * @param file
	 *            file
	 * @return file extension or empty string if file has not extension
	 */
	private static String getExtension(File file) {

		// Get file name
		String fileName = file.getName();

		// Get file extension
		String fileExtension = getExtension(fileName);

		return fileExtension;

	}
	
	/**
	 * Get file extension
	 * 
	 * @param fileName
	 *            file name
	 * @return file extension
	 */
	public static String getExtension(String fileName) {

		int index = fileName.lastIndexOf(".");

		if (index != -1) {

			// Extract file extension
			String fileExtension = fileName.substring(index + 1);

			return fileExtension;

		} else {
			return "";
		}

	}
	
	/**
	 * Get file base name
	 * 
	 * @param file
	 *            file
	 * @return file base name
	 */
	public static String getBasename(File file) {

		// Get file name
		String fileName = file.getName();

		// Get file base name
		String fileBasename = getBasename(fileName);

		return fileBasename;

	}

	/**
	 * Get file base name
	 * 
	 * @param fileName
	 *            file name
	 * @return file base name
	 */
	public static String getBasename(String fileName) {

		int index = fileName.lastIndexOf(".");

		if (index != -1) {

			// Extract file base name
			String fileBasename = fileName.substring(0, index);

			return fileBasename;

		} else {
			return fileName;
		}

	}

}
