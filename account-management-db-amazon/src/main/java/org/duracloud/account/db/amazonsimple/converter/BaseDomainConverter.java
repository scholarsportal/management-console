/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 *          Date: Dec 2, 2010
 */
public abstract class BaseDomainConverter {

    protected static final String DELIM = ",";

    protected Logger log;

    private String domain;
    
    private static SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S Z");

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
        if (value != null) {
            try {
                intValue = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                log.error(idType + " ID value where " + altIdName + " ID = " +
                          altId + " is not a valid integer: " + value);
                intValue = -1;
            }
        }
        return intValue;
    }

    protected String asString(Date date) {
        return DATE_FORMAT.format(date);
    }

    protected Date dateFromString(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            log.error("Unable to parse the following string as a date: {} ",
                      dateString);
        }

        return null;
    }

    protected String asString(int intVal) {
        return String.valueOf(intVal);
    }

    /**
     * This method formats a set of Integers as follows:
     * int1,int2,intN
     *
     * @param intSet set of integers
     * @return the string value of the set
     */
    protected String idsAsString(Set<Integer> intSet) {
        StringBuilder builder = new StringBuilder();
        if(null != intSet) {
            for(Integer in : intSet) {
                if(builder.length() > 0) {
                    builder.append(DELIM);
                }
                builder.append(in.toString());
            }
        }
        return builder.toString();
    }

    /**
     * This method takes a String containing a list of integers in the format:
     * int1,int2,intN
     * and produces a Set of Integer values.
     *
     * @param intList listing of integer values
     * @return Set of Integers
     */
    protected Set<Integer> idsFromString(String intList) {
        Set<Integer> set = new HashSet<Integer>();
        if(intList != null) {
            String[] splitValue = intList.split(DELIM);
            for(String instanceId : splitValue) {
                try {
                    set.add(Integer.valueOf(instanceId));
                } catch(NumberFormatException e) {
                    log.error("ID value is not a valid integer: " + instanceId);
                }
            }
        }
        return set;
    }
    
}
