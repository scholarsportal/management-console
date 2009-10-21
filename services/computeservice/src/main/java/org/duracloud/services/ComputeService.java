
package org.duracloud.services;

public interface ComputeService {

    public enum ServiceStatus {STARTED, STOPPED};

    public void start() throws Exception;

    public void stop() throws Exception;

    public String describe() throws Exception;

    public String getServiceId() throws Exception;

    public ServiceStatus getServiceStatus() throws Exception;
}
