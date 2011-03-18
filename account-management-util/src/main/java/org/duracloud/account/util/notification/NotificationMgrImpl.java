/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.notification;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.notification.Emailer;
import org.duracloud.notification.NotificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: 3/17/11
 */
public class NotificationMgrImpl implements NotificationMgr {

    private final Logger log = LoggerFactory.getLogger(NotificationMgrImpl.class);

    private NotificationFactory factory;
    private String fromAddress;
    private boolean isInitialized;

    public NotificationMgrImpl(NotificationFactory factory,
                               String fromAddress) {
        this.factory = factory;
        this.fromAddress = fromAddress;
        this.isInitialized = false;
    }

    @Override
    public void initialize(AmaConfig config) {
        factory.initialize(config.getUsername(), config.getPassword());
        isInitialized = true;
    }

    @Override
    public Emailer getEmailer() {
        if (!isInitialized) {
            String msg = "NotificationFactory !initialized.";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }

        return factory.getEmailer(fromAddress);
    }

}
