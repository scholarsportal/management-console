/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.sys.impl;

import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.util.config.McConfig;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * This class monitors application events and notifies the system admin(s).
 *
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class SystemMonitorImpl extends EventMonitorBase {

    private Logger log = LoggerFactory.getLogger(SystemMonitorImpl.class);

    private Set<String> recipients = new HashSet<>();

    public SystemMonitorImpl(NotificationMgr notificationMgr,
                             McConfig config) {
        super(notificationMgr);

        String adminEmail = config.getNotificationAdminAddress();
        if (null != adminEmail) {
            recipients.clear();
            recipients.add(adminEmail);
        }
    }

    @Override
    protected String buildSubj(AccountCreationInfo acctInfo) {
        return "New DuraCloud Account: " + acctInfo.getSubdomain();
    }

    @Override
    protected String buildBody(AccountCreationInfo acctInfo,
                               DuracloudUser user) {
        log.debug("Building email for, user:{}, acct:{}",
                  user.getUsername(),
                  acctInfo.getSubdomain());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(out);

        printer.printf("A new DuraCloud account has been created.%n%n");
        printer.printf("username: %1$s%n", user.getUsername());
        printer.printf("subdomain: %1$s%n", acctInfo.getSubdomain());

        Set<StorageProviderType> providers =
            acctInfo.getSecondaryStorageProviderTypes();
        if (null != providers && providers.size() > 0) {
            printer.printf("storage providers: %n");
            printer.printf("    %1s%n", acctInfo.getPrimaryStorageProviderType());
            for (StorageProviderType provider : acctInfo.getSecondaryStorageProviderTypes()) {
                printer.printf("    %1s%n", provider);
            }
        }

        printer.println(AmaEndpoint.getUrl());
        printer.printf("%n%nYour friend,%nMichael Jackson%n");

        printer.close();
        return out.toString();
    }

    @Override
    protected String[] buildRecipients(DuracloudUser notUsed) {
        return recipients.toArray(new String[0]);
    }
}