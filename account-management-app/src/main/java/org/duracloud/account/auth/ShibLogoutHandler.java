/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.auth;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * This class clears the Shibboleth session if the webapp is a protected
 * resource.
 *
 * @author Andrew Woods
 *         Date: 7/6/12
 */
public class ShibLogoutHandler implements LogoutSuccessHandler {

    private Logger log = LoggerFactory.getLogger(ShibLogoutHandler.class);

    protected static final String logoutPath = "/Shibboleth.sso/Logout";
    protected static final String duracloudDomain = "duracloud.org";

    private RestHttpHelper restHelper;

    public ShibLogoutHandler() {
        this(new RestHttpHelper());
    }

    public ShibLogoutHandler(RestHttpHelper restHelper) {
        this.restHelper = restHelper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Authentication authentication)
        throws IOException, ServletException {
        // Determine host
        URI url = getRequestUrl(httpServletRequest);
        HttpHost httpHost = URIUtils.extractHost(url);

        String host = (null == httpHost ? "none" : httpHost.toString());
        log.debug("host: {}", host);

        // Log out of shib if necessary
        if (host.contains(duracloudDomain)) {
            log.debug("Logging out of shibboleth");
            RestHttpHelper.HttpResponse response = doShibLogout(host);

            String body = response.getResponseBody();
            if (null != body) {
                httpServletResponse.getOutputStream().write(body.getBytes());
                httpServletResponse.setContentType("text/html");

            } else {
                log.warn("Null response while logging out of shib: {}", host);
            }

        } else {
            log.debug("No shib logout performed");
        }
    }

    private URI getRequestUrl(HttpServletRequest httpServletRequest) {
        try {
            return new URI(httpServletRequest.getRequestURL().toString());
        } catch (Exception e) {
            throw new DuraCloudRuntimeException("Unable to create URI", e);
        }
    }

    private RestHttpHelper.HttpResponse doShibLogout(String hostText) {
        try {
            return restHelper.get(hostText + logoutPath);
        } catch (Exception e) {
            throw new DuraCloudRuntimeException("Unable to do shib logout", e);
        }
    }

}
