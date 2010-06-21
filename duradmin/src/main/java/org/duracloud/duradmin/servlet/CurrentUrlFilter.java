/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This filter sets the current request url in the request scope for convenient
 * use by views.
 * 
 * @author Daniel Bernstein
 * @version $Id$
 */
public class CurrentUrlFilter
        implements Filter {

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest r = (HttpServletRequest) request;
        String currentUrl = r.getRequestURI();
        String q = r.getQueryString();
        if (q != null) {
            currentUrl = currentUrl + "?" + q;
        }
        request.setAttribute("currentUrl", currentUrl);
        chain.doFilter(request, response);
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }
}
