/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.notification;

import org.duracloud.notification.Emailer;

/**
 * This interface defines the contract for retrieval of notification resources.
 *
 * @author Andrew Woods
 *         Date: 3/17/11
 */
public interface NotificationMgr {

    /**
     * This method returns an emailer resource
     *
     * @return emailer
     */
    public Emailer getEmailer();

    /**
     * Retrieves the configuration of the NotificationMgr
     * @return config
     */
    public NotificationMgrConfig getConfig();

}
