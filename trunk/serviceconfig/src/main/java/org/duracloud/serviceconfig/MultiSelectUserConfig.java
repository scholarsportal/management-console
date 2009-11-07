package org.duracloud.serviceconfig;

import java.util.List;



public class MultiSelectUserConfig extends SelectableUserConfig{

    public MultiSelectUserConfig(String name, String displayName, boolean required, List<Option> options){
        super(name,displayName, required, options);
    }
    
    public InputType getInputType() {
        return InputType.MULTISELECT;
    }
    
    

}
