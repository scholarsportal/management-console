package org.duracloud.storage.provider;

/**
 * A Task Provider performs tasks which are specific to a particular provider
 * implementation, and thus cannot be generalized as part of StorageProvider.
 *
 * @author: Bill Branan
 *          Date: May 20, 2010
 */
public interface TaskProvider {

    public String performTask(String taskName, String taskParameters);

    public String getTaskStatus(String taskName);

}
