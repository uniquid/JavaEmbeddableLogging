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
	 * Stop logger strategy and wait for its thread termination
	 * 
	 * @throws InterruptedException
	 *             if thread in interrupted waiting for thread termination
	 */
	public void stop() throws InterruptedException;
}
