/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance.domain;


import java.util.HashMap;
import java.util.Map;

/**
 * This class holds details about the health of a given DuraCloud instance.
 *
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class InstanceInfo {

    private static final String OK = "OK";

    private String subdomain;
    private String serverStatus;
    private Map<String, String> endpoints;

    public InstanceInfo(String subdomain) {
        this.subdomain = subdomain;
        this.serverStatus = OK;
        this.endpoints = new HashMap<String, String>();
    }

    /**
     * This method returns true if the instance has any errors with its hosted
     * web applications.
     *
     * @return true if the instance has errors
     */
    public boolean hasErrors() {
        if (!serverStatus.equals(OK)) {
            return true;
        }

        for (String status : endpoints.values()) {
            if (!status.equals(OK)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method records error for the web resource at the arg context path.
     *
     * @param contextPath of resource with error
     * @param error       message
     */
    public void setError(String contextPath, String error) {
        endpoints.put(contextPath, error);
    }

    public void setSuccess(String contextPath) {
        endpoints.put(contextPath, OK);
    }

    /**
     * This method records a server-level (i.e. instance-level) status.
     *
     * @param serverStatus of server
     */
    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(subdomain);
        sb.append(".duracloud.org status: ");

        if (!hasErrors()) {
            sb.append(OK);

        } else {
            sb.append("FAILURE");
            sb.append("\n");
            sb.append(getStatusText("\t"));
        }

        return sb.toString();
    }

    private String getStatusText(String indent) {
        StringBuilder errors = new StringBuilder();
        if (!serverStatus.equals(OK)) {
            errors.append(serverStatus);
            errors.append("\n\n");
        }

        for (String endpoint : endpoints.keySet()) {
            String status = endpoints.get(endpoint);
            errors.append(indent);
            errors.append("https://");
            errors.append(subdomain);
            errors.append(".duracloud.org/");
            errors.append(endpoint);
            errors.append(" (");
            errors.append(status);
            errors.append(")");
            errors.append("\n");
        }

        return errors.toString();
    }

}
