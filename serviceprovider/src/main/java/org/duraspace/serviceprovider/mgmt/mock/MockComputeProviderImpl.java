
package org.duraspace.serviceprovider.mgmt.mock;

import java.net.URL;

import org.duraspace.common.model.Credential;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.ServiceProvider;

public class MockComputeProviderImpl
        implements ServiceProvider {

    private final String instanceId = "mockInstanceId";

    private final String url = "http://www.instance.org";

    public InstanceDescription describeRunningInstance(Credential credential,
                                                       String instanceId,
                                                       String xmlProps) {
        return new MockInstanceDescription();
    }

    public URL getWebappURL(Credential credential,
                            String instanceId,
                            String xmlProps) throws Exception {
        if (!isInstanceRunning(credential, instanceId, xmlProps)) {
            throw new Exception("Mock web app is not running: no url!");
        }
        return new URL(url);
    }

    public boolean isInstanceBooting(Credential credential,
                                     String instanceId,
                                     String xmlProps) throws Exception {
        return false;
    }

    public boolean isInstanceRunning(Credential credential,
                                     String instanceId,
                                     String xmlProps) throws Exception {
        return this.instanceId.equals(instanceId);
    }

    public boolean isWebappRunning(Credential credential,
                                   String instanceId,
                                   String xmlProps) throws Exception {
        return this.instanceId.equals(instanceId);
    }

    public String start(Credential cred, String xmlProps) throws Exception {
        return instanceId;
    }

    public void stop(Credential credential, String instanceId, String xmlProps)
            throws Exception {
    }

}
