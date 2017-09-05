package org.gmagnotta.log.impl.filesystem;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import org.gmagnotta.log.LogEvent;
import org.gmagnotta.log.LogEventWriter;

public class FileSystemLoggerStrategy implements LogEventWriter {

	private LinkedList<LogEvent> list;
	private Thread thread;

	public FileSystemLoggerStrategy(FileSystemLogStore logStore) throws FileNotFoundException {

		this.list = new LinkedList<LogEvent>();
		
		thread = new Thread(new FileSystemSpooler(logStore, list));
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public void write(LogEvent log) {
		
		synchronized (list) {
			
			list.add(log);
			list.notify();
			
		}
		
	}

	@Override
	public void stop() throws InterruptedException {

		// Interrupt spooler
		thread.interrupt();
		
	}

}
