package org.duracloud.serviceconfig.user;

public class TextUserConfig extends UserConfig {

    private static final long serialVersionUID = 5635327521932472393L;

    private String value;

    public TextUserConfig(String name, String displayName, String value) {
        super(name, displayName);
        this.value = value;
    }

    public TextUserConfig(String name, String displayName) {
        super(name, displayName);
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

    public String getDisplayValue(){
        return this.value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextUserConfig)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        TextUserConfig that = (TextUserConfig) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
