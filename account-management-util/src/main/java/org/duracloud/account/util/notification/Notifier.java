/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.notification;

import org.duracloud.account.common.domain.AmaEndpoint;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.notification.Emailer;

/**
 * @author: Bill Branan
 * Date: 8/1/11
 */
public class Notifier {

    private Emailer emailer;

    public Notifier(Emailer emailer) {
        this.emailer = emailer;
    }

    public void sendNotificationCreateNewUser(DuracloudUser user) {
        String subject = "DuraCloud Account Management: Profile Created";
        StringBuilder message = new StringBuilder();
        message.append("Thank you for creating your personal user ");
        message.append("profile with DuraCloud. Your username is ");
        message.append(user.getUsername());
        message.append(getProfileBaseMsg());
        sendEmail(subject, message.toString(), user.getEmail());
    }

    public void sendNotificationPasswordReset(DuracloudUser user,
                                               String newPassword) {
        String subject = "DuraCloud Account Management: Password Reset";
        StringBuilder message = new StringBuilder();
        message.append("Your password has been reset. Please use the ");
        message.append("following password to log into DuraCloud: ");
        message.append(newPassword);
        message.append(getProfileBaseMsg());
        sendEmail(subject, message.toString(), user.getEmail());
    }

    private String getProfileBaseMsg() {
        StringBuilder message = new StringBuilder();
        message.append(".\n\nTo access and/or change your profile ");
        message.append("information (including your password) or view ");
        message.append("any associated DuraCloud accounts, please ");
        message.append("visit: ");
        message.append(AmaEndpoint.getUrl());
        message.append("\n\n");
        message.append("The DuraCloud team");
        return message.toString();
    }

    public void sendNotificationRedeemedInvitation(DuracloudUser user,
                                                   String adminEmail) {
        String subject = "DuraCloud Account Management: Invitation Redeemed";
        StringBuilder message = new StringBuilder();
        message.append("The following user has accepted your DuraCloud ");
        message.append("account invitation: ");
        message.append(user.getUsername());
        message.append(". To edit the permissions of this user, please visit ");
        message.append(AmaEndpoint.getUrl());
        message.append("\n\n");
        message.append("The DuraCloud team");
        sendEmail(subject, message.toString(), adminEmail);
    }

    private void sendEmail(String subject, String message, String emailAddr) {
        try {
            emailer.send(subject, message, emailAddr);
        } catch (Exception e) {
            String msg =
                "Error: Unable to send email with subject: " + subject +
                " to address: " + emailAddr;
            throw new UnsentEmailException(msg, e);
        }
    }

}
