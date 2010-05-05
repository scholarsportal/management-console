package org.duracloud.duraservice.domain;

/**
 * Contains a service
 *
 * @author Bill Branan
 */
public class Service {

    private String id = null;
    private String description = null;
    private boolean deployed = false;
    private String deploymentLocation = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public String getDeploymentLocation() {
        return deploymentLocation;
    }

    public void setDeploymentLocation(String deploymentLocation) {
        this.deploymentLocation = deploymentLocation;
    }


}