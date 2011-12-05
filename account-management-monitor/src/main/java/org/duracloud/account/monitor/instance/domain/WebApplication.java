/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.domain;

import org.duracloud.account.monitor.error.UnexpectedResponseException;
import org.duracloud.appconfig.domain.Application;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class adds the ability to 'ping' an Application.
 *
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class WebApplication extends Application {

    private Logger log = LoggerFactory.getLogger(WebApplication.class);

    public WebApplication(String host,
                          String port,
                          String context,
                          RestHttpHelper restHelper) {
        super(host, port, context, restHelper);
    }

    /**
     * This method 'pings' the arg context path of this Application and checks
     * that the response status code equals the provided arg status code.
     *
     * @param path       to ping of this Application
     * @param statusCode expected
     * @throws UnexpectedResponseException if actual response status code does
     *                                     not match arg status code
     */
    public void ping(String path, int statusCode)
        throws UnexpectedResponseException {
        String url = super.getBaseUrl() + path;

        RestHttpHelper.HttpResponse response = null;
        try {
            response = super.getRestHelper().get(url);

        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append("Error in Webapplication, with ");
            error.append("restHttpHelper.get(");
            error.append(url);
            error.append("): " + e.getMessage());
            log.error(error.toString());
            throw new DuraCloudRuntimeException(error.toString());
        }

        // Was the response status the expected value?
        int responseCode = response.getStatusCode();
        if (statusCode != responseCode) {
            StringBuilder error = new StringBuilder();
            error.append("Unexpected status code: ");
            error.append(response.getStatusCode());
            error.append(", expected (");
            error.append(statusCode);
            error.append(") for url: ");
            error.append(url);
            log.error(error.toString());
            throw new UnexpectedResponseException(statusCode, responseCode);
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("Performed ping of url: ");
            msg.append(url);
            msg.append(" and received expected status code: ");
            msg.append(response.getStatusCode());
            log.debug(msg.toString());
        }
    }

}
