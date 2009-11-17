package org.duracloud.serviceconfig;

import org.duracloud.serviceconfig.user.UserConfig;

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

    /** Unique identifier for this service */
    private int id;

    /** DuraStore ID for retrieving the service binaries */
    private String contentId;

    /** User-friendly Service name */
    private String displayName;

    /** Release version number of the service software */
    private String version;

    /** User configuration version, checked at deployment time */
    private String userConfigVersion;

    /** Text description of service capabilities */
    private String description;

    /** Specifies number of deployments of this service that are allowed */
    private int maxDeploymentsAllowed;

    /** The default system configuration options */
    private List<SystemConfig> systemConfigs;

    /** The default user configuration options */
    private List<UserConfig> userConfigs;

    /** Includes information necessary to deploy a new service of this type */
    private List<DeploymentOption> deploymentOptions;

    /** Includes information about existing deployments of this service */
    private List<Deployment> deployments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMaxDeploymentsAllowed() {
        return maxDeploymentsAllowed;
    }

    public void setMaxDeploymentsAllowed(int maxDeploymentsAllowed) {
        this.maxDeploymentsAllowed = maxDeploymentsAllowed;
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

    public String getHost() {
        return getSystemConfigValueByName("host");
    }

    public Integer getPort() {
        String value = getSystemConfigValueByName("port");
        return value != null ? Integer.valueOf(value) : null;
    }

    private String getSystemConfigValueByName(String name) {
        for (SystemConfig s : systemConfigs) {
            if (s.getName().equals(name)) {
                return s.getValue();
            }
        }
        return null;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public ServiceInfo clone() {
        // TODO: Actually perform clone
        return this;
    }
}
