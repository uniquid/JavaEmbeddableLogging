package org.gmagnotta.log.impl.filesystem;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 * A log file name comparator
 */
public class LogFileNameComparator implements Comparator<String> {
	
	boolean reverse;
	
	public LogFileNameComparator() {
		this(false);
	}
	
	public LogFileNameComparator(boolean reverse) {
		this.reverse = reverse;
	}

	@Override
	public int compare(String logFileName1, String logFileName2) {

		try {

			// Get log file date
			Date logFileDate1 = LogFileNameUtil.getLogFileDate(logFileName1);
			Date logFileDate2 = LogFileNameUtil.getLogFileDate(logFileName2);
	
			// Get result
			int result = compare(logFileDate1, logFileDate2);

			if (reverse) {
				return -result;
			}
			
			return result;

		} catch (ParseException ex) {
			throw new RuntimeException(
					"an error occurs comparing log file names", ex);
		}

	}

	/**
	 * Compare given log file date
	 * 
	 * @param logFileDate1
	 *            the first log file date
	 * @param logFileDate2
	 *            the second log file date
	 * @return an integer that is the difference of the two date in milliseconds
	 */
	private int compare(Date logFileDate1, Date logFileDate2) {

		// Get time
		long time1 = logFileDate1.getTime();
		long time2 = logFileDate2.getTime();

		// Get result
		int result = new Long(time2 - time1).intValue();

		return result;

	}

}
