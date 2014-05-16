/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Erik Paulsson
 *         Date: 7/10/13
 */
@Entity
public class ServiceRepository extends BaseEntity {

    public enum ServiceRepositoryType {
        VERIFIED,
        PUBLIC,
        PRIVATE;
    }

    /**
     * The type of the service repository. There are currently three options
     * 1. Verified - a service repository which includes services provided
     *               directly by DuraCloud staff or verified by DuraCloud staff
     * 2. Public   - a service repository which includes publicly available
     *               services which have not been verified by DuraCloud
     * 3. Private  - a service repository which hosts services which are not
     *               available for public use
     */
    @Enumerated(EnumType.STRING)
    private ServiceRepositoryType serviceRepositoryType;

    /**
     * The servicePlan of the repository.
     * This defines which services are available.
     */
    @Enumerated(EnumType.STRING)
    private ServicePlan servicePlan;

    /**
     * The name of the host from which a service repository is served
     */
    private String hostName;

    /**
     * The ID of the space in which the contents of a service repository reside
     */
    private String spaceId;

    /**
     * The ID of the service-xml defining services available in this plan
     */
    private String serviceXmlId;

    /**
     * The version of the DuraCloud services available is the repository
     */
    private String version;

    /**
     * The username necessary to log in to the service repository
     */
    private String username;

    /**
     * The password necesary to log in to the service repository
     */
    private String password;

    public ServiceRepositoryType getServiceRepositoryType() {
        return serviceRepositoryType;
    }

    public void setServiceRepositoryType(ServiceRepositoryType serviceRepositoryType) {
        this.serviceRepositoryType = serviceRepositoryType;
    }

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

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

    public String getServiceXmlId() {
        return serviceXmlId;
    }

    public void setServiceXmlId(String serviceXmlId) {
        this.serviceXmlId = serviceXmlId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
