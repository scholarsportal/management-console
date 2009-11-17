package org.duracloud.serviceconfig.user;

import org.duracloud.serviceconfig.user.Option;

import java.util.List;



public class SingleSelectUserConfig extends SelectableUserConfig{
    private static final long serialVersionUID = -2912715735337021361L;

    public SingleSelectUserConfig(String name, String displayName, boolean required, List<Option> options){
        super(name,displayName, options);
        
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
