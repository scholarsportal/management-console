package org.duracloud.serviceconfig.user;

import java.io.Serializable;

/**
 * * This class holds service config info supplied by the user.
 *
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public abstract class UserConfig implements Serializable {

    private static final long serialVersionUID = -6727102713612538135L;

    /**
     * Directs UI input type.
     */
    public enum InputType {
        SINGLESELECT, MULTISELECT, TEXT;

        public String getName() {
            return name();
        }
    }

    private int id;
    private String name;
    private String displayName;
    private boolean required; // NOTE: this not persisted in underlying XML

    public UserConfig(String name, String displayName, boolean required) {
        this.name = name;
        this.displayName = displayName;
        this.required = required;
    }

    public abstract InputType getInputType();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequired() {
        return required;
    }

}
