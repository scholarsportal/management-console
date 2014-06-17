/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.sys.impl;

import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * This class monitors application events and notifies the customer.
 *
 * @author Andrew Woods
 *         Date: 3/22/11
 */
public class CustomerMonitorImpl extends EventMonitorBase {

    private Logger log = LoggerFactory.getLogger(CustomerMonitorImpl.class);

    public CustomerMonitorImpl(NotificationMgr notificationMgr) {
        super(notificationMgr);
    }

    @Override
    protected String buildSubj(AccountCreationInfo acctInfo) {
        return "Successful creation of DuraCloud account: " +
            acctInfo.getSubdomain();
    }

    @Override
    protected String buildBody(AccountCreationInfo acctInfo,
                               DuracloudUser user) {
        log.debug("Building email for, user:{}, acct:{}",
                  user.getUsername(),
                  acctInfo.getSubdomain());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(out);

        printer.printf("Hello, %1$s.%n%n", user.getFirstName());
        printer.printf("Welcome to your new DuraCloud account. ");
        printer.printf("You have registered under the subdomain:%n");
        printer.printf("    %1$s%n%n", acctInfo.getSubdomain());

        Set<StorageProviderType> providers =
            acctInfo.getSecondaryStorageProviderTypes();
        if (null != providers && providers.size() > 0) {
            printer.printf("with the following storage providers: %n");
            printer.printf("    %1s%n", acctInfo.getPrimaryStorageProviderType());
            for (StorageProviderType provider : acctInfo.getSecondaryStorageProviderTypes()) {
                printer.printf("    %1s%n", provider);
            }
        }

        printer.print("Feel free to invite additional users to the account, ");
        printer.print("monitor your service status, and update your account ");
        printer.printf("preferences at: %n");
        printer.printf("    %1$s%n%n", AmaEndpoint.getUrl());

        printer.printf("%nThank you,%nDuraCloud Team%n");

        printer.close();
        return out.toString();
    }

    @Override
    protected String[] buildRecipients(DuracloudUser user) {
        return new String[]{user.getEmail()};
    }
}
