package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.hibernate.validator.constraints.NotBlank;

import java.text.MessageFormat;

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

    private ServicePlan servicePlan;

    private ServiceRepositoryType serviceRepoType;

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

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public ServiceRepositoryType getServiceRepoType() {
        return serviceRepoType;
    }

    public void setServiceRepoType(ServiceRepositoryType serviceRepoType) {
        this.serviceRepoType = serviceRepoType;
    }

    @Override
    public String toString() {
        String template = "{0}(hostName={1},spaceId={2}," +
                          "xmlId={3},version={4},userName={5},password={6}, " +
                          "servicePlan={7},serviceRepoType={8})";
        return MessageFormat.format(template, 
                                    getClass().getSimpleName(),
                                    this.hostName,
                                    this.spaceId,
                                    this.xmlId,
                                    this.version,
                                    this.userName,
                                    this.password,
                                    this.servicePlan,
                                    this.serviceRepoType);
    }
}
