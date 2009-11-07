package org.duracloud.serviceconfig;

import java.util.List;



public class SingleSelectUserConfig extends SelectableUserConfig{
    public SingleSelectUserConfig(String name, String displayName, boolean required, List<Option> options){
        super(name,displayName, required, options);
        
        boolean hasSelected = false;
        for(Option o : options){
            if(o.isSelected()){
                if(hasSelected){
                    throw new IllegalArgumentException("the option list contains more than one selected option");
                }else{
                    hasSelected = true;
                }
            }
        }
    }
    public InputType getInputType() {
        return InputType.SINGLESELECT;
    }

}
