package org.duracloud.serviceconfig;


/**
 * This class holds config option details.
 */
public class Option {
    private String displayName;
    private String value;
    private boolean isSelected;

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
        return isSelected;
    }

    protected void setSelected(boolean selected) {
        isSelected = selected;
    }
}
