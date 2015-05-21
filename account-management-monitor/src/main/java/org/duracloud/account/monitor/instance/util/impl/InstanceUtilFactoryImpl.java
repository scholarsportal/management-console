/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
