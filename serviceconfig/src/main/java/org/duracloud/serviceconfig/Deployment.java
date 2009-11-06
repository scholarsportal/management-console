package org.duracloud.serviceconfig;

/**
 * This class holds the description and state of a service deployment host option.
 *
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class Deployment {

    /**
     * Is this the primary host? new one? existing secondary host?
     */
    public enum LocationType {
        PRIMARY, NEW, EXISTING;
    }

    public enum StateType {
        ACTIVE, AVAILABLE, UNAVAILABLE, SELECTED;
    }

    private String name;
    private LocationType locationType;
    private StateType stateType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
