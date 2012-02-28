/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
public class AccountClusterDescriptor {
    public AccountClusterDescriptor(int id, String name) {
        super();
        if (id < 1) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        if (StringUtils.isBlank(name)){
            throw new IllegalArgumentException("name must not be blank.");
        }
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        AccountClusterDescriptor acd = (AccountClusterDescriptor)o;
        
        return (acd.id == this.id);
        
    }
    
    @Override
    public int hashCode() {
        return id % 31;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
