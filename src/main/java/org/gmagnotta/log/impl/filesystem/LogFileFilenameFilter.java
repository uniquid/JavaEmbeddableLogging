package org.gmagnotta.log.impl.filesystem;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A log file name filter
 */
public class LogFileFilenameFilter implements FilenameFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File folder, String filename) {

		// Get accepted
		boolean accepted = LogFileNameUtil.isValid(filename);

		return accepted;

	}

}
