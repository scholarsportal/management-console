/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class GlobalPropertiesForm {
    @NotNull
    private String instanceNotificationTopicArn;

    public String getInstanceNotificationTopicArn() {
        return instanceNotificationTopicArn;
    }

    public void setInstanceNotificationTopicArn(
            String instanceNotificationTopicArn) {
        this.instanceNotificationTopicArn = instanceNotificationTopicArn;
    }       
}
