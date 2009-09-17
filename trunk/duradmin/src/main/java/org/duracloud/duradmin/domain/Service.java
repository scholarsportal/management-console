package org.duracloud.duradmin.domain;

import java.io.Serializable;

import java.util.Map;

public class Service implements Serializable {

    private static final long serialVersionUID = 5271039343141055224L;

    private String serviceId;
    private Map<String, String> config;
    private String status;
    private String serviceHost;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

}
