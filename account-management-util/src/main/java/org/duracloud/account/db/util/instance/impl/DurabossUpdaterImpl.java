/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance.impl;

import org.apache.commons.httpclient.HttpStatus;
import org.duracloud.account.db.util.error.DurabossUpdateException;
import org.duracloud.account.db.util.instance.DurabossUpdater;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages updating the DuraBoss actions.
 *
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public class DurabossUpdaterImpl implements DurabossUpdater {

    private Logger log = LoggerFactory.getLogger(DurabossUpdaterImpl.class);

    protected final static String port = "443";


    @Override
    public void startDuraboss(String host,
                              DurabossConfig durabossConfig,
                              RestHttpHelper restHelper) {
        // Verify duraboss has been initialized.
        if (!isInitialized(host, durabossConfig, restHelper)) {
            throw new DurabossUpdateException(host, "not initialized");
        }
    }



    @Override
    public void stopDuraboss(String host,
                             DurabossConfig durabossConfig,
                             RestHttpHelper restHelper) {
        // Verify duraboss has been initialized.
        if (!isInitialized(host, durabossConfig, restHelper)) {
            throw new DurabossUpdateException(host, "not initialized");
        }

    }



    private boolean isInitialized(String host,
                                  DurabossConfig config,
                                  RestHttpHelper restHelper) {
        String url = getInitUrl(host, config);
        try {
            return restHelper.get(url).getStatusCode() == HttpStatus.SC_OK;

        } catch (Exception e) {
            log.warn("Error checking host: " + host + ", " + e.getMessage());
            return false;
        }
    }

    private String getInitUrl(String host, DurabossConfig config) {
        StringBuilder url = new StringBuilder("https://");
        url.append(host);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(config.getDurabossContext());
        url.append(config.getInitResource());
        return url.toString();
    }


 

}
