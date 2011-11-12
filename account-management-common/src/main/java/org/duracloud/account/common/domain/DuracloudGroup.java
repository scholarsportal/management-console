/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *         Date: Nov 11, 2011
 */
public class DuracloudGroup extends BaseDomainData implements Comparable<DuracloudGroup> {
    private String name;
    private Set<DuracloudUser> users;

    public DuracloudGroup(String name) {
        if(StringUtils.isBlank(name)){
            throw new IllegalArgumentException("name parameter must not be blank");
        }
        this.name = name;
    }

    public void setUsers(Collection<DuracloudUser> groupUsers) {
        if(this.users == null){
            this.users = new HashSet<DuracloudUser>();
        }
        
        this.users.clear();
        this.users.addAll(groupUsers);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DuracloudGroup) {
            return this.name.equals(((DuracloudGroup) obj).name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public int compareTo(DuracloudGroup o) {
        return o.name.compareTo(this.name);
    }

    public Set<DuracloudUser> getUsers() {
        if (this.users == null) {
            return null;
        }

        return Collections.unmodifiableSet(this.users);
    }

    public void addUser(DuracloudUser user) {
        if (this.users == null) {
            this.users = new HashSet<DuracloudUser>();
        }
        this.users.add(user);
    }

    public DuracloudUser removeUser(String username) {
        if (this.users == null) {
            return null;
        }

        for (DuracloudUser user : this.users) {
            if (user.getUsername().equals(username)) {
                if(this.users.remove(user)){
                    return user;
                }
            }
        }
        
        return null;
    }
}
