/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.email;

import java.util.List;

import org.duracloud.notification.AmazonNotificationFactory;
import org.duracloud.notification.Emailer;

/**
 * @author Andrew Woods
 * Date: 7/6/11
 */
public class EmailUtilImpl implements EmailUtil {

    private Emailer emailer;
    private String[] recipients;

    public EmailUtilImpl(Emailer emailer, List<String> recipients) {
        this.emailer = emailer;
        this.recipients = recipients.toArray(new String[] {});
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
