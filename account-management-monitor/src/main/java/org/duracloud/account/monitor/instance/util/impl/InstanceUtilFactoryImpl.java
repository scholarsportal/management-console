/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.util.impl;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class InstanceUtilFactoryImpl implements InstanceUtilFactory {

    private Map<String, InstanceUtil> utils;

    public InstanceUtilFactoryImpl() {
        utils = new HashMap<String, InstanceUtil>();
    }

    @Override
    public InstanceUtil getInstanceUtil(AccountInfo acct) {
        String subdomain = acct.getSubdomain();

        InstanceUtil util = utils.get(subdomain);
        if (null == util) {
            util = new InstanceUtilImpl(subdomain);
            utils.put(subdomain, util);
        }
        return util;
    }

}
