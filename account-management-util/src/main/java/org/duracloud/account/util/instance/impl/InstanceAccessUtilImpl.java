/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.instance.InstanceAccessUtil;
import org.duracloud.account.util.instance.InstanceUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Bill Branan
 * Date: 4/1/11
 */
public class InstanceAccessUtilImpl implements InstanceAccessUtil, InstanceUtil {

    private Logger log = LoggerFactory.getLogger(InstanceAccessUtilImpl.class);

    private static final String PROTOCOL = "https://";
    private static final int SLEEP_TIME = 10000;

    @Override
    public void waitInstanceAvailable(String hostname, long timeout) {
        long start = System.currentTimeMillis();
        while(!instanceAvailable(hostname)) {
            long now = System.currentTimeMillis();
            if(now - start > timeout) {
                String error = "Instance at host " + hostname +
                   " was not available prior to wait timeout of " +
                   timeout + " milliseconds.";
                throw new DuracloudInstanceUpdateException(error);
            } else {
                sleep(SLEEP_TIME);
            }
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException e) {
        }
    }

    @Override
    public boolean instanceAvailable(String hostname) {
        RestHttpHelper restHelper = new RestHttpHelper();

        String duradminUrl =
            PROTOCOL + hostname + "/" + DURADMIN_CONTEXT;
        String durastoreUrl =
            PROTOCOL + hostname + "/" + DURASTORE_CONTEXT + "/stores";
        String duraserviceUrl =
            PROTOCOL + hostname + "/" + DURASERVICE_CONTEXT + "/services";
        String durareportUrl =
            PROTOCOL + hostname + "/" + DURAREPORT_CONTEXT +
                "/storagereport/info";

        try {
            if(!checkResponse(restHelper.get(duradminUrl)) ||
               !checkResponse(restHelper.get(durastoreUrl)) ||
               !checkResponse(restHelper.get(duraserviceUrl)) ||
               !checkResponse(restHelper.get(durareportUrl))) {
                return false;
            } else {
                return true;
            }
        } catch(Exception e) {
            return false;
        }
    }

    private boolean checkResponse(RestHttpHelper.HttpResponse response) {
        int statusCode = response.getStatusCode();
        if(statusCode == 200 || statusCode == 401) {
            return true;
        } else {
            return false;
        }
    }

}
