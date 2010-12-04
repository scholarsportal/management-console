/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import org.slf4j.Logger;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public abstract class BaseDomainConverter {

    protected static final String DELIM = ",";    

    protected Logger log;

    private String domain;

    public String getDomain() {
        if (null == domain) {
            domain = "unknown-domain";
        }
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    protected int idFromString(String value,
                               String idType,
                               String altIdName,
                               int altId) {
        int intValue = -1;
        if(value != null) {
            try {
                intValue = Integer.valueOf(value);
            } catch(NumberFormatException e) {
                log.error(idType + " ID value where " + altIdName + "=" +
                          altId + " is not a valid integer: " + value);
                intValue = -1;
            }
        }
        return intValue;
    }

}
