/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util.impl;

import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.monitor.error.UnsupportedStorageProviderException;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.duracloud.account.monitor.hadoop.util.HadoopUtilFactory;
import org.duracloud.storage.domain.StorageProviderType;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopUtilFactoryImpl implements HadoopUtilFactory {

    private Map<Integer, HadoopUtil> utils;

    public HadoopUtilFactoryImpl() {
        utils = new HashMap<Integer, HadoopUtil>();
    }

    @Override
    public HadoopUtil getHadoopUtil(StorageProviderAccount storageAcct)
        throws UnsupportedStorageProviderException {
        StorageProviderType type = storageAcct.getProviderType();
        if (!StorageProviderType.AMAZON_S3.equals(type)) {
            throw new UnsupportedStorageProviderException(type);
        }

        int acctId = storageAcct.getId();

        HadoopUtil util = utils.get(acctId);
        if (null == util) {
            util = new HadoopUtilImpl(storageAcct.getUsername(),
                                      storageAcct.getPassword());
            utils.put(acctId, util);
        }
        return util;
    }

}
