
package org.duraspace.serviceprovider.mgmt;

public interface ServiceProvider {

    /**
     * This method asynchronously starts an instance-image with provided id.
     *
     * @param imageId
     * @return ID of running instance.
     */
    public String start(String imageId) throws Exception;

    /**
     * This method stops the running instance.
     */
    public void stop(String instanceId) throws Exception;

    /**
     * This method returns true if the instance is successfully running.
     *
     * @param instanceId
     * @return
     */
    public boolean isInstanceRunning(String instanceId) throws Exception;

    /**
     * This method returns true if the webapp of provide instance is
     * successfully running.
     *
     * @param instanceId
     * @return
     */
    public boolean isWebappRunning(String instanceId) throws Exception;

    /**
     * This method retrieves description of initiated instance.
     *
     * @param instanceId
     * @return
     */
    public InstanceDescription describeRunningInstance(String instanceId)
            throws Exception;

}
