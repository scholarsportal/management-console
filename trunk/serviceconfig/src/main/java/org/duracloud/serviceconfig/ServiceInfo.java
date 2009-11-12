package org.duracloud.serviceconfig;

import java.io.Serializable;
import java.util.List;



/**
 * This class is the container for all of the settings and options available for 
 *  a service.
 *
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class ServiceInfo implements Serializable, Cloneable{

    private static final long serialVersionUID = -7958760599324208594L;

    private String id;
    private String displayName;
    private String serviceName;
    private String description;

    /** The default system configuration options */
    private List<SystemConfig> systemConfigs;

    /** The default user configuration options */
    private List<UserConfig> userConfigs;

    /** User configuration version, checked at deployment time */
    private String userConfigVersion;

    /** Includes information necessary to deploy a new service of this type */
    private List<DeploymentOption> deploymentOptions;

    /** Includes information about existing deployments of this service */
    private List<Deployment> deployments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<SystemConfig> getSystemConfigs() {
        return systemConfigs;
    }

    public void setSystemConfigs(List<SystemConfig> systemConfigs) {
        this.systemConfigs = systemConfigs;
    }

    public List<UserConfig> getUserConfigs() {
        return userConfigs;
    }

    public void setUserConfigs(List<UserConfig> userConfigs) {
        this.userConfigs = userConfigs;
    }

    public String getUserConfigVersion() {
        return userConfigVersion;
    }

    public void setUserConfigVersion(String userConfigVersion) {
        this.userConfigVersion = userConfigVersion;
    }

    public List<DeploymentOption> getDeploymentOptions() {
        return deploymentOptions;
    }

    public void setDeploymentOptions(List<DeploymentOption> deploymentOptions) {
        this.deploymentOptions = deploymentOptions;
    }

    public List<Deployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(List<Deployment> deployments) {
        this.deployments = deployments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHost(){
        return getSystemConfigValueByName("host");
    }

    public Integer getPort(){
        String value = getSystemConfigValueByName("port");
        return value != null ? Integer.valueOf(value) : null;
    }
    private String getSystemConfigValueByName(String name) {
        for(SystemConfig s : systemConfigs){
            if(s.getName().equals(name)){
                return s.getValue();
            }
        }
        return null;
    }

    public ServiceInfo clone() {
        // TODO: Actually perform clone
        return this;
    }
}
