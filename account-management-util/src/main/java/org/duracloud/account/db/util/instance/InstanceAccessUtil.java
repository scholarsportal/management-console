/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.instance;

/**
 * @author: Bill Branan
 * Date: 4/4/11
 */
public interface InstanceAccessUtil {

    /**
     * Returns when an instance is available or after the timeout expires,
     * whichever occurs first.
     *
     * @param hostname of the instance to be checked
     * @param timeout the maximum number of milliseconds to wait before returning
     */
    public void waitInstanceAvailable(String hostname, long timeout);

    /**
     * Checks to see if a DuraCloud instance at the given host name is
     * available. Availability is determined by all of the DuraCloud
     * applications returning an expected response code when queried.
     *
     * @param hostname of the instance to be checked
     * @return
     */
    public boolean instanceAvailable(String hostname);    

}
