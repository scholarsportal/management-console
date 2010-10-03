package org.duracloud.account.util;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

/**
 * An interface for controlling a deployed duracloud instance
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public interface DuracloudInstanceService {
	public static enum State {
		/*
		 * instance in the process of starting up.
		 */
		STARTING,
		/*
		 * instance is running.
		 */
		RUNNING,
		/*
		 * instance is in the process of stopping
		 */
		STOPPING,

		/*
		 * instance is not running, but ready to be started.
		 */
		STOPPED;

	}
	
	/**
	 * 
	 * @return
	 */
	public InetAddress getAddress();

	/**
	 * 
	 * @return
	 */
	public String getStatusMessage();

	/**
	 * 
	 * @return
	 */
	public Long getUptime();

	/**
	 * 
	 * @return
	 */
	public List<LogMessage> getLogMessages(Date from, Date to);
	

	/**
	 * Returns the state of the Duracloud Instance.
	 * 
	 * @return
	 */
	public State getState();


	/**
	 * Starts the instance.
	 * 
	 * @throws IllegalStateException
	 *             when method is invoked when the instance is not in the
	 *             READY state
	 */
	public void start()
			throws IllegalStateException;

	/**
	 * Stops the instance.
	 * 
	 * @throws IllegalStateException
	 *             when method is invoked when the instance is not in the
	 *             RUNNING state
	 */
	public void stop()
			throws IllegalStateException;

	/**
	 * Restarts the instance. If instance is READY, it simply starts it up.
	 * Otherwise the instance is stopped and started.
	 * 
	 * @throws IllegalStateException
	 *             when the instance is not in the READY or RUNNING state
	 */
	public void restart()
			throws IllegalStateException;

}
