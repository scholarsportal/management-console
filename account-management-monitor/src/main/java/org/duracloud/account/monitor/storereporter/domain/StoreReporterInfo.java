/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.domain;

/**
 * This class holds details about the health of a given DuraCloud instance's
 * Storage Reporter.
 *
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterInfo {

    private static final String OK = "OK";

    private String subdomain;
    private String reporterStatus;

    public StoreReporterInfo(String subdomain) {
        this.subdomain = subdomain;
        this.reporterStatus = OK;
    }

    /**
     * This method returns true if the Storage Reporter has any errors.
     *
     * @return true if the Storage Reporter has errors
     */
    public boolean hasErrors() {
        return !reporterStatus.equals(OK);
    }

    /**
     * This method records error for the web resource.
     *
     * @param error message
     */
    public void setError(String error) {
        reporterStatus = error;
    }

    public void setSuccess() {
        reporterStatus = OK;
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
            sb.append(reporterStatus);
        }

        return sb.toString();
    }

}
