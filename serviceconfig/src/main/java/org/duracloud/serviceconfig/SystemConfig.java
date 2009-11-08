package org.duracloud.serviceconfig;

import java.io.Serializable;

/**
 * This class holds service config info supplied by the user.
 *  
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class SystemConfig implements Serializable{

    private static final long serialVersionUID = -3280385789614105156L;

    private String name;
    private String value;
    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
