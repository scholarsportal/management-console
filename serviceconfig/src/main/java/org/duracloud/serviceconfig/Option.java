package org.duracloud.serviceconfig;

import java.io.Serializable;


/**
 * This class holds config option details.
 */
public class Option implements Serializable{

    private static final long serialVersionUID = -2243245528826127669L;

    private String displayName;
    private String value;
    private boolean selected;

    public Option(String displayName, String value, boolean selected) {
        super();
        this.displayName = displayName;
        this.value = value;
        this.selected = selected;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    protected void setSelected(boolean selected) {
       this.selected = selected;
    }
}
