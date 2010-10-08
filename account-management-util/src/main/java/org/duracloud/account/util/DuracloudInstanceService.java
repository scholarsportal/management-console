/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

/**
 * An interface for controlling a deployed duracloud instance
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface DuracloudInstanceService {

    public static enum State {
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED;
    }

    public InetAddress getAddress();

    public String getStatusMessage();

    public Long getUptime();

    public List<LogMessage> getLogMessages(Date from, Date to);

    /**
     * Returns the state of the Duracloud Instance.
     */
    public State getState();

    /**
     * Starts the instance.
     *
     * @throws IllegalStateException when method is invoked when the instance is not in the READY
     *                               state
     */
    public void start() throws IllegalStateException;

    /**
     * Stops the instance.
     *
     * @throws IllegalStateException when method is invoked when the instance is not in the
     *                               RUNNING state
     */
    public void stop() throws IllegalStateException;

    /**
     * Restarts the instance. If instance is READY, it simply starts it up.
     * Otherwise the instance is stopped and started.
     *
     * @throws IllegalStateException when the instance is not in the READY or RUNNING state
     */
    public void restart() throws IllegalStateException;

}
