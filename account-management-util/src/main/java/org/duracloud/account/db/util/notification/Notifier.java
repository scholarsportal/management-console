/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.db.util.notification;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.util.error.UnsentEmailException;
import org.duracloud.notification.Emailer;

import java.util.Date;

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
                                               String redemptionCode, 
                                               Date date) {
        String subject = "DuraCloud Account Management: Password Reset";
        StringBuilder message = new StringBuilder();
        message.append("You have requested to reset your password.");
        message.append("Click on this following link to change your password:\n");
        message.append(AmaEndpoint.getUrl()+"/users/change-password/"+redemptionCode+"\n");
        message.append("This link is good for one password change only and will expire on " + date);
        message.append("\n\n");
        message.append("The DuraCloud team");
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
    
    public void sendNotificationUserAddedToAccount(DuracloudUser user, AccountInfo accountInfo) {
        String subject = "You are now a member of "
                         + accountInfo.getOrgName() + "'s DuraCloud Account.";
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ");
        sb.append(user.getFirstName());
        sb.append(" ");
        sb.append(user.getLastName());
        sb.append(":\n\n");

        sb.append("You have been added to the DuraCloud account which is managed by ");
        sb.append(accountInfo.getOrgName());
        if (StringUtils.isNotBlank(accountInfo.getDepartment())) {
            sb.append(", ");
            sb.append(accountInfo.getDepartment());
        }
        sb.append(".  You may now log into the ");
        sb.append(accountInfo.getAcctName());
        sb.append(" account at https://");
        sb.append(accountInfo.getSubdomain());
        sb.append(".duracloud.org with the following username: " + user.getUsername() + ".");
        sb.append("\n\n");
        sb.append("Please note that the links above are not supported by ");
        sb.append("Internet Explorer version 8 or prior. It is recommended ");
        sb.append("that you use either Internet Explorer version 9 (or ");
        sb.append("later) or another web browser to access DuraCloud.");
        sb.append("\n");
        sb.append("\n");
        sb.append("The DuraCloud team");

        sendEmail(subject, sb.toString(), user.getEmail());
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
