package org.duracloud.serviceconfig;


public class TextUserConfig extends UserConfig{

    private String value;
    
    public TextUserConfig (String name, String displayName, boolean required, String value){
        super(name, displayName,required);
        this.value = value;
    }
    
    public TextUserConfig(String name, String displayName, boolean required){
        super(name,displayName, required);
    }
    
    public InputType getInputType() {
        return InputType.TEXT;
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
