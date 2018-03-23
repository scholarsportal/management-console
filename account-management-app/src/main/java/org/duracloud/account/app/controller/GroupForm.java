/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

public class GroupForm {

    private String[] availableUsernames;
    private String[] groupUsernames;
    private Action action;

    public static enum Action {
        ADD,
        REMOVE,
        SAVE;
    }

    public GroupForm() {
        this.availableUsernames = null;
        this.groupUsernames = null;
    }

    public String[] getAvailableUsernames() {
        return availableUsernames;
    }

    public void setAvailableUsernames(String[] availableUsernames) {
        this.availableUsernames = availableUsernames;
    }

    public String[] getGroupUsernames() {
        return groupUsernames;
    }

    public void setGroupUsernames(String[] groupUsernames) {
        this.groupUsernames = groupUsernames;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void reset() {
        this.availableUsernames = null;
        this.groupUsernames = null;
    }
}
