package org.duracloud.serviceconfig.user;

public class TextUserConfig extends UserConfig {

    private static final long serialVersionUID = 5635327521932472393L;

    private String value;
    
    public TextUserConfig (String name, String displayName, String value){
        super(name, displayName);
        this.value = value;
    }
    
    public TextUserConfig(String name, String displayName){
        super(name,displayName);
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
