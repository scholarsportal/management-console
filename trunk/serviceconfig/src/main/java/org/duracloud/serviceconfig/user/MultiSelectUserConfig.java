package org.duracloud.serviceconfig.user;

import java.util.List;


public class MultiSelectUserConfig extends SelectableUserConfig {

    private static final long serialVersionUID = 8515015818197420269L;

    public MultiSelectUserConfig(String name,
                                 String displayName,
                                 List<Option> options) {
        super(name, displayName, options);
    }

    public InputType getInputType() {
        return InputType.MULTISELECT;
    }

}
