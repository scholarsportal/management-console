/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.sys.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.sys.EventMonitor;
import org.duracloud.notification.Emailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew Woods
 *         Date: 3/22/11
 */
public abstract class EventMonitorBase implements EventMonitor {

    private Logger log = LoggerFactory.getLogger(SystemMonitorImpl.class);

    private NotificationMgr notificationMgr;

    public EventMonitorBase(NotificationMgr notificationMgr) {
        this.notificationMgr = notificationMgr;
    }

    @Override
    public void accountCreated(AccountCreationInfo acctInfo,
                               DuracloudUser user) {
        log.debug("Acct created for, user:{}, acct:{}",
                  user.getUsername(),
                  acctInfo.getSubdomain());

        Emailer emailer = notificationMgr.getEmailer();

        String subj = buildSubj(acctInfo);
        String body = buildBody(acctInfo, user);
        String[] recipients = buildRecipients(user);

        emailer.send(subj, body, recipients);
    }

    protected abstract String buildSubj(AccountCreationInfo acctInfo);

    protected abstract String buildBody(AccountCreationInfo acctInfo,
                                        DuracloudUser user);

    protected abstract String[] buildRecipients(DuracloudUser user);
}
