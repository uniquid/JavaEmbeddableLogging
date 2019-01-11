package org.gmagnotta.log;

/**
 * Interface that allow to implement Strategy design Pattern 
 */
public interface LogEventWriter {
	
	/**
	 * Perform log
	 * 
	 * @param log
	 */
	public void write(LogEvent log);

	/**
	 * Stop logger strategy
	 */
	public void stop();

	/**
	 * Set log name
	 */
	public void setLogName(String logName);
}
