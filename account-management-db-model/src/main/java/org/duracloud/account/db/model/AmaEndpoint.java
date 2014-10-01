/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.model;

import org.duracloud.account.config.McConfig;
/**
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class AmaEndpoint {
    private McConfig config;
    public AmaEndpoint(McConfig config){
         this.config = config;
    }

    public String getHost() {
        return config.getMcHost();
    }

    public String getCtxt() {
        return config.getMcContext();
    }

    public  String getPort() {
        return config.getMcPort();
    }

    public  String getUrl() {
        String proto = getPort().equals("443") ? "https" : "http";
        return proto + "://" + getHost() + ":" + getPort() + "/" + getCtxt();
    }

}
