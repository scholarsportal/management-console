/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.sys.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AmaEndpoint;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.domain.Initable;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class monitors application events and notifies the system admin(s).
 *
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class SystemMonitorImpl extends EventMonitorBase implements Initable {

    private Logger log = LoggerFactory.getLogger(SystemMonitorImpl.class);

    private Set<String> recipients = new HashSet<String>();

    public SystemMonitorImpl(NotificationMgr notificationMgr) {
        super(notificationMgr);
    }

    public void initialize(AmaConfig config) {
        Collection emails = config.getAdminAddresses();
        if (null != emails && emails.size() > 0) {
            Iterator<String> itr = emails.iterator();
            while (itr.hasNext()) {
                recipients.add(itr.next());
            }
        }
    }

    @Override
    protected String buildSubj(AccountInfo acctInfo) {
        return "New DuraCloud Account: " + acctInfo.getSubdomain();
    }

    @Override
    protected String buildBody(AccountInfo acctInfo, DuracloudUser user) {
        log.debug("Building email for, user:{}, acct:{}",
                  user.getUsername(),
                  acctInfo.getSubdomain());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(out);

        printer.printf("A new DuraCloud account has been created.%n%n");
        printer.printf("username: %1$s%n", user.getUsername());
        printer.printf("subdomain: %1$s%n", acctInfo.getSubdomain());

        Set<StorageProviderType> providers = acctInfo.getStorageProviders();
        if (null != providers && providers.size() > 0) {
            printer.printf("storage providers: %n");
            for (StorageProviderType provider : acctInfo.getStorageProviders()) {
                printer.printf("    %1s%n", provider);
            }
        }

        String host = AmaEndpoint.getHost();
        String port = AmaEndpoint.getPort();
        String ctxt = AmaEndpoint.getCtxt();
        printer.println("http://" + host + ":" + port + "/" + ctxt);
        printer.printf("%n%nYour friend,%nMichael Jackson%n");

        printer.close();
        return out.toString();
    }

    @Override
    protected String[] buildRecipients(DuracloudUser notUsed) {
        return recipients.toArray(new String[0]);
    }
}
