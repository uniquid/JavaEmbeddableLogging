package org.gmagnotta.log;

/**
 * A dummy throwable for debug purpose
 * 
 */
class LogStackTraceThrowable extends Throwable {

	private static final long serialVersionUID = 1L;

	/**
	 * Create new log stack trace throwable
	 * 
	 * @param message
	 *            the message
	 */
	LogStackTraceThrowable(String message) {
		super(
				"this is exception is only intended for stack trace dump: please ignore it: "
						.concat(message));
	}

}
