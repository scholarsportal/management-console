package org.duracloud.serviceconfig;

import java.io.Serializable;
import java.util.List;

/**
 * This class holds the name, status, and configuration of an deployed service.
 *
 * @author Bill Branan
 *         Date: Nov 9, 2009
 */
public class Deployment implements Serializable{

    private static final long serialVersionUID = -5554753103296039412L;

    public enum StatusType {
        STOPPED, STARTED;
    }

    /** The name of the host on which this service is deployed */
    private String name;

    /** The status of this deployed service */
    private StatusType status;

    /** The system configuration settings for this deployed service */
    private List<SystemConfig> systemConfigs;

    /** The user configuration settings for this deployed service */
    private List<UserConfig> userConfigs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
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
}
