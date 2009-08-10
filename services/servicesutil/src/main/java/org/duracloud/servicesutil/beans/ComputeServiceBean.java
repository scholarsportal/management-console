
package org.duracloud.servicesutil.beans;

public class ComputeServiceBean {

    private String serviceName;

    public ComputeServiceBean() {
    }

    public ComputeServiceBean(String name) {
        serviceName = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
