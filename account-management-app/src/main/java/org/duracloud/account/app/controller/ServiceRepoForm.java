package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

public class ServiceRepoForm {
    @NotBlank(message = "You must specify a host name.")
    private String hostName;

    @NotBlank(message = "You must specify a space id.")
    private String spaceId;

    @NotBlank(message = "You must specify a service xml id.")
    private String xmlId;

    @NotBlank(message = "You must specify a version.")
    private String version;

    @NotBlank(message = "You must specify a user name.")
    private String userName;

    @NotBlank(message = "You must specify a password.")
    private String password;

    @NotBlank(message = "You must specify a service plan.")
    private String servicePlan;

    @NotBlank(message = "You must specify a service repo type.")
    private String serviceRepoType;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(String servicePlan) {
        this.servicePlan = servicePlan;
    }

    public String getServiceRepoType() {
        return serviceRepoType;
    }

    public void setServiceRepoType(String serviceRepoType) {
        this.serviceRepoType = serviceRepoType;
    }
}
