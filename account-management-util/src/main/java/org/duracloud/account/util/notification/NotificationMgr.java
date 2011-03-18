/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.notification;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.notification.Emailer;

/**
 * This interface defines the contract for retrieval of notification resources.
 *
 * @author Andrew Woods
 *         Date: 3/17/11
 */
public interface NotificationMgr {

    /**
     * This method initializes the NotificationMgr.
     *
     * @param config with initialization elements
     */
    public void initialize(AmaConfig config);

    /**
     * This method returns an emailer resource
     *
     * @return emailer
     */
    public Emailer getEmailer();
}
