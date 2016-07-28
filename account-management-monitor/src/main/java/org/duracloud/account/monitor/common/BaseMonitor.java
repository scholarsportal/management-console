/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.common;

import org.duracloud.common.model.Credential;
import org.slf4j.Logger;

/**
 * @author Bill Branan
 *         Date: 4/16/13
 */
public abstract class BaseMonitor {

    protected Logger log;


    protected Credential getRootCredential() {
        String rootUsername = System.getProperty("monitor.username", "monitor");
        
        String rootPassword = System.getProperty("monitor.password", "password");
        return new Credential(rootUsername, rootPassword);
    }


}
