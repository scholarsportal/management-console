package org.duracloud.duradmin.util;

import java.io.Serializable;


public  class NameValuePair implements Serializable{

    private static final long serialVersionUID = 1L;

    public NameValuePair(String name, Object value) {
        super();
        this.name = name;
        Value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public Object getValue() {
        return Value;
    }
    private String name;
    private Object Value;
}