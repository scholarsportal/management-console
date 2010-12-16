/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;

/**
 * @author: Bill Branan Date: Dec 2, 2010
 */
public abstract class BaseDomainConverter {

    protected static final String DELIM = ",";

    protected Logger log;

    private String domain;
    
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S Z");

    public String getDomain() {
        if (null == domain) {
            domain = "unknown-domain";
        }
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    protected int idFromString(
        String value, String idType, String altIdName, int altId) {
        int intValue = -1;
        if (value != null) {
            try {
                intValue = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                log.error(idType
                    + " ID value where " + altIdName + "=" + altId
                    + " is not a valid integer: " + value);
                intValue = -1;
            }
        }
        return intValue;
    }

    protected String asString(Date date) {
        return DATE_FORMAT.format(date);
    }

    protected  Date dateFromString(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            log.error(
                "Unable to parse the following string as a date: {} ",
                dateString);
        }

        return null;
    }

    protected String asString(int intVal) {
        return String.valueOf(intVal);
    }
}
