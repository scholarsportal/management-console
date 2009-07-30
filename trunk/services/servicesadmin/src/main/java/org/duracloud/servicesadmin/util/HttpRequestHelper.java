
package org.duracloud.servicesadmin.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHelper {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestHelper.class);

    public static String SERVICE_ID = "service-id";

    public static String getServiceIdParameter(HttpServletRequest request)
            throws Exception {
        String serviceId = request.getParameter(SERVICE_ID);
        if (serviceId == null || serviceId.length() < 1) {
            String msg = "'" + SERVICE_ID + "' parameter was empty.";
            log.error(msg);
            throw new Exception(msg);
        }
        return serviceId;
    }
}
