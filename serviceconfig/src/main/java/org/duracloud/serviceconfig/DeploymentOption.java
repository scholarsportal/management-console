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

    public enum State {
        ACTIVE, AVAILABLE, UNAVAILABLE, SELECTED;
    }

    private String displayName;
    private LocationType locationType;
    private State state;

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}