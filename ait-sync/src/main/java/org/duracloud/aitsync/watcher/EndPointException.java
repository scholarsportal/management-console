package org.duracloud.aitsync.watcher;

/**
 * 
 * @author Daniel Bernstein 
 * Date: 12/24/2012
 * 
 */
public class EndPointException extends Exception {
    public EndPointException(String message, Throwable t) {
        super(message, t);
    }
}
