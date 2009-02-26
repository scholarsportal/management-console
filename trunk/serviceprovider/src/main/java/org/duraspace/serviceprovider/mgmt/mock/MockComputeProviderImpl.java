
package org.duraspace.serviceprovider.mgmt.mock;

import java.net.URL;

import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.ServiceProvider;
import org.duraspace.serviceprovider.mgmt.ServiceProviderProperties;

public class MockComputeProviderImpl
        implements ServiceProvider {

    private final String instanceId = "mockInstanceId";

    private final String url = "http://www.instance.org";

    public InstanceDescription describeRunningInstance(String instanceId) {
        return new MockInstanceDescription();
    }

    public URL getWebappURL(String instanceId) throws Exception {
        if (!isInstanceRunning(instanceId)) {
            throw new Exception("Mock web app is not running: no url!");
        }
        return new URL(url);
    }

    public boolean isInstanceBooting(String instanceId) throws Exception {
        return false;
    }

    public boolean isInstanceRunning(String instanceId) throws Exception {
        return this.instanceId.equals(instanceId);
    }

    public boolean isWebappRunning(String instanceId) throws Exception {
        return this.instanceId.equals(instanceId);
    }

    public String start(ServiceProviderProperties props) throws Exception {
        return instanceId;
    }

    public void stop(String instanceId) throws Exception {
    }

}
