package org.gmagnotta.log;

/**
 * Interface that allow to implement Strategy design Pattern 
 */
public interface LoggerStrategy {
	
	/**
	 * Return log level
	 * 
	 * @return
	 */
	public LogLevel getLogLevel();
	
	/**
	 * Set log level
	 * @param logLevel
	 */
	public void setLogLevel(LogLevel logLevel);
	
	/**
	 * Perform log
	 * 
	 * @param log
	 */
	public void log(LogMessage log);

	/**
	 * Stop logger strategy and wait for its thread termination
	 * 
	 * @throws InterruptedException
	 *             if thread in interrupted waiting for thread termination
	 */
	public void stop() throws InterruptedException;
}
