/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.InstanceType;

/**
 * @author: Bill Branan
 * Date: 4/6/11
 */
public class AccountInstanceForm {
    private String version;
    private InstanceType instanceType = InstanceType.SMALL;
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public InstanceType getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(InstanceType instanceType) {
        this.instanceType = instanceType;
    }
    
}
