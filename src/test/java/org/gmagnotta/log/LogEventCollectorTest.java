package org.gmagnotta.log;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class LogEventCollectorTest {
	
	private static LogEventCollector logEventCollector;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logEventCollector = LogEventCollector.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logEventCollector.stop();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		final LogEvent event = new LogEvent(LogLevel.INFO, "test", new Date(), "thread", "test");
		
		logEventCollector.addLogEventWriter(new LogEventWriter() {
			
			@Override
			public void write(LogEvent log) {
				Assert.assertEquals(event, log);
			}
			
			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setLogName(String logName) {
				// TODO Auto-generated method stub
			}

		});
		
		logEventCollector.addLogEvent(event);
	}

}
