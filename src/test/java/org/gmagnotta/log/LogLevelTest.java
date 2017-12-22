package org.gmagnotta.log;

import org.junit.Assert;
import org.junit.Test;

public class LogLevelTest {
	
	@Test
	public void testLevels() throws Exception {
		
		Assert.assertTrue(LogLevel.TRACE.isHigherOrEqual(LogLevel.TRACE));
		Assert.assertFalse(LogLevel.TRACE.isHigherOrEqual(LogLevel.DEBUG));
		Assert.assertFalse(LogLevel.TRACE.isHigherOrEqual(LogLevel.INFO));
		Assert.assertFalse(LogLevel.TRACE.isHigherOrEqual(LogLevel.WARNING));
		Assert.assertFalse(LogLevel.TRACE.isHigherOrEqual(LogLevel.ERROR));
		
		Assert.assertTrue(LogLevel.DEBUG.isHigherOrEqual(LogLevel.DEBUG));
		Assert.assertTrue(LogLevel.DEBUG.isHigherOrEqual(LogLevel.TRACE));
		Assert.assertFalse(LogLevel.DEBUG.isHigherOrEqual(LogLevel.INFO));
		Assert.assertFalse(LogLevel.DEBUG.isHigherOrEqual(LogLevel.WARNING));
		Assert.assertFalse(LogLevel.DEBUG.isHigherOrEqual(LogLevel.ERROR));
		
		Assert.assertTrue(LogLevel.INFO.isHigherOrEqual(LogLevel.INFO));
		Assert.assertTrue(LogLevel.INFO.isHigherOrEqual(LogLevel.TRACE));
		Assert.assertTrue(LogLevel.INFO.isHigherOrEqual(LogLevel.DEBUG));
		Assert.assertFalse(LogLevel.INFO.isHigherOrEqual(LogLevel.WARNING));
		Assert.assertFalse(LogLevel.INFO.isHigherOrEqual(LogLevel.ERROR));
		
		Assert.assertTrue(LogLevel.WARNING.isHigherOrEqual(LogLevel.WARNING));
		Assert.assertTrue(LogLevel.WARNING.isHigherOrEqual(LogLevel.TRACE));
		Assert.assertTrue(LogLevel.WARNING.isHigherOrEqual(LogLevel.DEBUG));
		Assert.assertTrue(LogLevel.WARNING.isHigherOrEqual(LogLevel.INFO));
		Assert.assertFalse(LogLevel.WARNING.isHigherOrEqual(LogLevel.ERROR));
		
		Assert.assertTrue(LogLevel.ERROR.isHigherOrEqual(LogLevel.ERROR));
		Assert.assertTrue(LogLevel.ERROR.isHigherOrEqual(LogLevel.TRACE));
		Assert.assertTrue(LogLevel.ERROR.isHigherOrEqual(LogLevel.DEBUG));
		Assert.assertTrue(LogLevel.ERROR.isHigherOrEqual(LogLevel.INFO));
		Assert.assertTrue(LogLevel.ERROR.isHigherOrEqual(LogLevel.WARNING));
		
	}

}
