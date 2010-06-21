package org.duracloud.storage.provider;

/**
 * @author: Bill Branan
 * Date: May 21, 2010
 */
public interface TaskRunner {

    public String getName();

    public String performTask(String taskParameters);

    public String getStatus();
    
}
