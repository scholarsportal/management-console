/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication.util;

import org.duracloud.account.monitor.duplication.DuplicationMonitor;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Bill Branan
 *         Date: 4/19/13
 */
public class DuplicationPropReader {

    private static final String PREFIX = "duplication.";
    private static final String HOST = ".host";
    private static final String SPACES = ".spaces";
    protected static final String ALL_SPACES = DuplicationMonitor.ALL_SPACES;

    public Map<String, String> readDupProps(Properties props) {
        Map<String, String> dupHosts = new HashMap<>();
        Enumeration propNames = props.propertyNames();
        while(propNames.hasMoreElements()) {
            String propName = (String)propNames.nextElement();
            if(propName.startsWith(PREFIX)) {
                String propValue = getProperty(props, propName);
                if (propName.endsWith(HOST)) {
                    String spacesProp = propName.replace(HOST, SPACES);
                    String spacesVal = getProperty(props, spacesProp);
                    dupHosts.put(propValue, spacesVal);
                } else if (propName.endsWith(SPACES)) {
                } else {
                    dupHosts.put(propValue, ALL_SPACES);
                }
            }
        }
        return dupHosts;
    }

    private String getProperty(Properties props, String key) {
        String property = props.getProperty(key);
        if (null == property) {
            throw new DuraCloudRuntimeException("Property not found: " + key);
        }
        return property;
    }

}
