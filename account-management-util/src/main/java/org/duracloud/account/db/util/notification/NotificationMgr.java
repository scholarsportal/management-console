/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
