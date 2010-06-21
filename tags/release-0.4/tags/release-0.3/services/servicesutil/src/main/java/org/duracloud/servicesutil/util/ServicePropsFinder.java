package org.duracloud.servicesutil.util;

import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Dec 18, 2009
 */
public interface ServicePropsFinder {

    public Map<String, String> getProps(String serviceId);
}
