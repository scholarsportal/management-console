/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.util;

import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.monitor.error.UnsupportedStorageProviderException;

/**
 * This interface defines the contract of a factory that creates instances of
 * HadoopUtil.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public interface HadoopUtilFactory {

    /**
     * This method returns an HadoopUtil object for the arg storage provider
     * account.
     *
     * @param storageAcct for which a HadoopUtil is wanted
     * @return hadoopUtil
     * @throws UnsupportedStorageProviderException
     *          if the arg storageAcct does
     *          not support Hadoop
     */
    public HadoopUtil getHadoopUtil(StorageProviderAccount storageAcct)
        throws UnsupportedStorageProviderException;
}
