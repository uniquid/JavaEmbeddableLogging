package org.gmagnotta.log;

/**
 * Interface that allow to implement Strategy design Pattern 
 */
public interface LogEventFilter {
	
	/**
	 * Filter a log
	 * 
	 * @param logEvent
	 */
	public boolean accept(LogEvent logEvent);

}
