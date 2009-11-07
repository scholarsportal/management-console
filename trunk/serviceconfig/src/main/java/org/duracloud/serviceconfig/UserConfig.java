package org.duracloud.serviceconfig;

import java.util.List;

/**
 * * This class holds service config info supplied by the user.
 * 
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public abstract class UserConfig {

    /**
     * Directs UI input type.
     */
    public enum InputType {
        SINGLESELECT, MULTISELECT, TEXT;
    }


    private String name;
    private String displayName;
    private boolean required;


    public UserConfig (String name, String displayName, boolean required){
        this.name = name;
        this.displayName = displayName;
        this.required = required;
    }
    
    public abstract InputType getInputType();
    
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
