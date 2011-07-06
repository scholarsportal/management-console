/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.duracloud.account.db.backup.util.EmailUtil;
import org.duracloud.notification.AmazonNotificationFactory;
import org.duracloud.notification.Emailer;

import java.util.List;

/**
 * @author Andrew Woods
 *         Date: 7/6/11
 */
public class EmailUtilImpl implements EmailUtil {

    private Emailer emailer;
    private String[] recipients;

    public EmailUtilImpl(Emailer emailer, List<String> recipients) {
        this.emailer = emailer;
        this.recipients = recipients.toArray(new String[]{});
    }

    public EmailUtilImpl(String accessKey,
                         String secretKey,
                         String fromAddress,
                         List<String> recipients) {
        this(getEmailFactory(accessKey, secretKey).getEmailer(fromAddress),
             recipients);
    }

    private static AmazonNotificationFactory getEmailFactory(String accessKey,
                                                             String secretKey) {
        AmazonNotificationFactory factory = new AmazonNotificationFactory();
        factory.initialize(accessKey, secretKey);
        return factory;
    }

    @Override
    public void sendEmail(String subject, String body) {
        emailer.send(subject, body, recipients);
    }
}
