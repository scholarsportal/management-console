package org.duracloud.serviceconfig;

import java.util.Collections;
import java.util.List;


public abstract class SelectableUserConfig extends UserConfig{

    private static final long serialVersionUID = 560564548722671194L;

    private List<Option> options;
    
    public SelectableUserConfig(String name, String displayName, boolean required, List<Option> options){
        super(name,displayName, required);
        this.options = Collections.unmodifiableList(options);
    }

    public List<Option> getOptions() {
        return options;
    }

    public void deselectAll(){
        for(Option o : options){
            o.setSelected(false);
        }
    }
    
    public void select(Option option){
        if(this instanceof SingleSelectUserConfig){
            deselectAll();
        }
        option.setSelected(true);
    }
    
    public void select(String optionValue){
        for(Option o : options){
            if(o.getValue().equals(optionValue)){
                select(o);
                return;
            }
        }
    }

}
