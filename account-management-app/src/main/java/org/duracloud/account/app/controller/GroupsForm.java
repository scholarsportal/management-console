/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

/**
 * 
 * @author Daniel Bernstein
 *          Date: Nov 14, 2011
 *
 */
public class GroupsForm {
    private String groupName;
    private String[] groupNames;
    private Action action;
    public enum Action {
        ADD,
        REMOVE;
    }
    
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String[] getGroupNames() {
        return groupNames;
    }
    public void setGroupNames(String[] groupNames) {
        this.groupNames = groupNames;
    }
    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }
    
}
