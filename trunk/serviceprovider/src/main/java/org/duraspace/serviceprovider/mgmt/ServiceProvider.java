
package org.duraspace.serviceprovider.mgmt;

import java.net.URL;

public interface ServiceProvider {

    /**
     * This method asynchronously starts an instance-image with provided id.
     *
     * @param imageId
     * @return ID of running instance.
     */
    public String start(ServiceProviderProperties props) throws Exception;

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
     * This method returns true if the webapp of provided instance is
     * successfully running.
     *
     * @param instanceId
     * @return
     */
    public boolean isWebappRunning(String instanceId) throws Exception;

    /**
     * This method returns true if the webapp of provided instance is currently
     * booting.
     *
     * @param instanceId
     * @return
     */
    public boolean isInstanceBooting(String instanceId) throws Exception;

    /**
     * This method returns the URL of the instancewebapp on the instance with
     * the provided id.
     *
     * @param instanceId
     * @return
     * @throws Exception
     */
    public URL getWebappURL(String instanceId) throws Exception;

    /**
     * This method retrieves description of initiated instance.
     *
     * @param instanceId
     * @return
     */
    public InstanceDescription describeRunningInstance(String instanceId);

}
