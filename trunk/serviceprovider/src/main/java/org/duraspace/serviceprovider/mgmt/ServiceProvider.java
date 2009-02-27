
package org.duraspace.serviceprovider.mgmt;

import java.net.URL;

import org.duraspace.common.model.Credential;

public interface ServiceProvider {

    /**
     * This method asynchronously starts an instance-image with provided id.
     *
     * @param imageId
     * @return ID of running instance.
     */
    public String start(Credential credential, ServiceProviderProperties props)
            throws Exception;

    /**
     * This method stops the running instance.
     */
    public void stop(Credential credential, String instanceId) throws Exception;

    /**
     * This method returns true if the instance is successfully running.
     *
     * @param instanceId
     * @return
     */
    public boolean isInstanceRunning(Credential credential, String instanceId)
            throws Exception;

    /**
     * This method returns true if the webapp of provided instance is
     * successfully running.
     *
     * @param instanceId
     * @return
     */
    public boolean isWebappRunning(Credential credential, String instanceId)
            throws Exception;

    /**
     * This method returns true if the webapp of provided instance is currently
     * booting.
     *
     * @param instanceId
     * @return
     */
    public boolean isInstanceBooting(Credential credential, String instanceId)
            throws Exception;

    /**
     * This method returns the URL of the instancewebapp on the instance with
     * the provided id.
     *
     * @param instanceId
     * @return
     * @throws Exception
     */
    public URL getWebappURL(Credential credential, String instanceId)
            throws Exception;

    /**
     * This method retrieves description of initiated instance.
     *
     * @param instanceId
     * @return
     */
    public InstanceDescription describeRunningInstance(Credential credential,
                                                       String instanceId);

}
