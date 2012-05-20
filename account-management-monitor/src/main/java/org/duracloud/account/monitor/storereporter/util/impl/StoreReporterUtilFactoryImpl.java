/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.common.model.Credential;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterUtilFactoryImpl implements StoreReporterUtilFactory {

    private Map<String, StoreReporterUtil> utils;
    private int thresholdDays;

    public StoreReporterUtilFactoryImpl(int thresholdDays) {
        this.thresholdDays = thresholdDays;
        this.utils = new HashMap<String, StoreReporterUtil>();
    }

    @Override
    public StoreReporterUtil getStoreReporterUtil(AccountInfo acct,
                                                  Credential credential) {
        String subdomain = acct.getSubdomain();

        StoreReporterUtil util = utils.get(subdomain);
        if (null == util) {
            util = new StoreReporterUtilImpl(subdomain,
                                             credential,
                                             thresholdDays);
            utils.put(subdomain, util);
        }
        return util;
    }

}
