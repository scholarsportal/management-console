package org.duracloud.duradmin.view;

/**
 * 
 *
 * @author Daniel Bernstein
 * @version $Id$
 */
public class FlashMessage {
    public enum Type {
        INFO,
        WARNING,
        ERROR;
    }
    
    private String message;
    
    private Type type;

    public FlashMessage(String message) {
        this(message, Type.INFO);
    }
    
    public FlashMessage(String message, Type type) {
        super();
        this.message = message;
        this.type = type;
    }
    
    public Type getType(){
        return this.type;
    }

    public String getMessage(){
        return this.message;
    }
    
    public String getTypeAsString(){
        return this.type.name().toLowerCase();
    }
}

