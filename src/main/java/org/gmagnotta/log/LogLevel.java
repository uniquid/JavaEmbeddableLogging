package org.gmagnotta.log;

/**
 * Available log levels
 */
public enum LogLevel {
	
	TRACE,
	DEBUG,
	INFO,
	WARNING,
	ERROR;
	
	/**
	 * Returns true if this level is higher or equal to the parameter
	 * @param other other level to compare
	 * @return true if this level is higher or equals to other or false.
	 */
	public boolean isHigherOrEqual(LogLevel other) {

		if (this.compareTo(other) >= 0) {
			return true;
		}

		return false;

	}

}
