
package org.duraspace.computeprovider.mgmt;

import java.net.URL;

import java.util.Date;

public abstract class InstanceDescription {

    protected URL url;

    protected String provider;

    protected String instanceId;

    protected InstanceState state;

    protected Date launchTime;

    protected Exception exception;

    public boolean hasError() {
        return exception != null;
    }

    public URL getURL() {
        return url;
    }

    public String getProvider() {
        return provider;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public InstanceState getState() {
        return state;
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public Exception getException() {
        return exception;
    }

}
