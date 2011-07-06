/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util;

/**
 * This interface defines the contract for a utility which manages sending
 * emails.
 *
 * @author Andrew Woods
 *         Date: 7/6/11
 */
public interface EmailUtil {

    /**
     * This method sends an email to a pre-defined set of recipients from a
     * pre-defined sender with the arg subject and body.
     *
     * @param subject of email
     * @param body    of email
     */
    public void sendEmail(String subject, String body);
}
