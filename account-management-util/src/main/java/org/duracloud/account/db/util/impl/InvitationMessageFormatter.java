/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.model.UserInvitation;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class InvitationMessageFormatter {

    private UserInvitation invitation;
    private AmaEndpoint endpoint;
    public InvitationMessageFormatter(UserInvitation invitation,
            AmaEndpoint amaEndpoint) {
        this.invitation = invitation;
        this.endpoint = endpoint;
    }

    public String getSubject() {
        return "DuraCloud Account Invitation";
    }

    public String getBody() {
        StringBuilder sb = new StringBuilder();

        sb.append("You have been invited to join the DuraCloud account which is managed by ");
        sb.append(this.invitation.getAccountOrg());
        if(this.invitation.getAccountDep() != null && !this.invitation.getAccountDep().equals("")) {
            sb.append(", ");
            sb.append(this.invitation.getAccountDep());
        }
        sb.append(". After accepting this invitation, you will be given access to the ");
        sb.append(this.invitation.getAccountName());
        sb.append(" account and will be able to log in to https://");
        sb.append(this.invitation.getAccountSubdomain());
        sb.append(".duracloud.org. In order to join, please follow these instructions:");
        sb.append("\n\n");
        sb.append("1. If you do not have a personal user profile with ");
        sb.append("DuraCloud, please create one at: ");
        sb.append(getNewProfileURL());
        sb.append(" ");
        sb.append("If you already have a personal user profile with ");
        sb.append("DuraCloud, you may skip this step.");
        sb.append("\n");
        sb.append("2. Click on this link to log in to the DuraCloud ");
        sb.append("Management Console: ");
        sb.append(getRedemptionURL());
        sb.append("\n");
        sb.append("3. After logging in, you should see the details for the ");
        sb.append("DuraCloud account that you now have access to.");
        sb.append("\n\n");
        sb.append("Please note that the links above are not supported by ");
        sb.append("Internet Explorer version 8 or prior. It is recommended ");
        sb.append("that you use either Internet Explorer version 9 (or ");
        sb.append("later) or another web browser to access DuraCloud.");
        sb.append("\n\n");
        sb.append("If you have any issues accepting the invitation or ");
        sb.append("creating your personal user profile, please contact the ");
        sb.append("DuraCloud team by emailing your issue to ");
        sb.append("support@duracloud.org.");
        sb.append("\n");
        sb.append("\n");
        sb.append("The DuraCloud team");

        return sb.toString();
    }

    public String getRedemptionURL() {
        return endpoint.getUrl() + "/users/redeem/" + this.invitation.getRedemptionCode();
    }

    private String getNewProfileURL() {
        return endpoint.getUrl() + "/users/new";
    }

}
