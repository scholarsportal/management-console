package org.duracloud.serviceconfig;

import java.util.List;

/**
 * * This class holds service config info supplied by the user.
 * 
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class UserConfig {

    /**
     * Directs UI input type.
     */
    public enum InputType {
        SELECT, MULTISELECT, TEXT;
    }

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

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    private String name;
    private String displayName;
    private InputType inputType;
    private boolean isSelected;
    private List<Option> options;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

}
