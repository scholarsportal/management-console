/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.sys.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.config.McConfig;
import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class monitors application events and notifies the system admin(s).
 * 
 * @author Andrew Woods Date: 3/21/11
 */
public class SystemMonitorImpl extends EventMonitorBase {

    private Logger log = LoggerFactory.getLogger(SystemMonitorImpl.class);

    private Set<String> recipients = new HashSet<>();

    private AmaEndpoint amaEndpoint;

    public SystemMonitorImpl(NotificationMgr notificationMgr, McConfig config,
            AmaEndpoint amaEndpoint) {
        super(notificationMgr);

        String adminEmail = config.getNotificationAdminAddress();
        if (null != adminEmail) {
            recipients.clear();
            recipients.add(adminEmail);
        }

        this.amaEndpoint = amaEndpoint;
    }

    @Override
    protected String buildSubj(AccountCreationInfo acctInfo) {
        return "Successful creation of DuraCloud account: "
                + acctInfo.getSubdomain();
    }

    @Override
    protected String buildBody(AccountCreationInfo acctInfo) {
        log.debug("Building email for acct:{}", acctInfo.getSubdomain());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(out);

        printer.printf("Welcome to your new DuraCloud account. ");
        printer.printf("You have registered under the subdomain:%n");
        printer.printf("    %1$s%n%n", acctInfo.getSubdomain());

        Set<StorageProviderType> providers = acctInfo
                .getSecondaryStorageProviderTypes();
        if (null != providers && providers.size() > 0) {
            printer.printf("with the following storage providers: %n");
            printer.printf("    %1s%n",
                    acctInfo.getPrimaryStorageProviderType());
            for (StorageProviderType provider : acctInfo
                    .getSecondaryStorageProviderTypes()) {
                printer.printf("    %1s%n", provider);
            }
        }

        printer.print("Feel free to invite additional users to the account, ");
        printer.print("monitor your service status, and update your account ");
        printer.printf("preferences at: %n");
        printer.printf("    %1$s%n%n", amaEndpoint.getUrl());

        printer.printf("%nThank you,%nDuraCloud Team%n");

        printer.close();
        return out.toString();
    }

    @Override
    protected String[] buildRecipients() {
        return recipients.toArray(new String[0]);
    }
}
