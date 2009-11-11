package org.duracloud.serviceconfig;

import java.io.Serializable;

/**
 * This class holds the description and state of a service deployment host option.
 *
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class DeploymentOption implements Serializable{

    private static final long serialVersionUID = -5554753103296039413L;

    /**
     * Is this the primary host? new one? existing secondary host?
     */
    public enum LocationType {
        PRIMARY, NEW, EXISTING;
    }

    public enum StateType {
        AVAILABLE, UNAVAILABLE;
    }

    private String hostName;
    private String displayName;
    private LocationType locationType;
    private StateType stateType;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

}