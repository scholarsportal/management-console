/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *         Date: Nov 11, 2011
 */
public class DuracloudGroup extends BaseDomainData implements Comparable<DuracloudGroup> {

    public static final String PREFIX = "group-";

    /**
     * Group names must begin with PREFIX.
     */
    private String name;
    private Set<Integer> userIds;

    public DuracloudGroup(int id, String name) {
        this(id, name, null, 0);
    }

    public DuracloudGroup(int id, String name, Set<Integer> userIds) {
        this(id, name, userIds, 0);
    }

    public DuracloudGroup(int id,
                          String name,
                          Set<Integer> userIds,
                          int counter) {
        if (StringUtils.isBlank(name) || !name.startsWith(PREFIX)) {
            throw new IllegalArgumentException(
                "Name arg must begin with " + PREFIX + ", " + name);
        }

        this.id = id;
        this.name = name;
        this.userIds = userIds;
        this.counter = counter;
    }

    public String getName() {
        return this.name;
    }

    /**
     * This method returns the ids of the users belonging to this group.
     *
     * @return ids of users of this group or an empty set
     */
    public Set<Integer> getUserIds() {
        if (null == userIds) {
            userIds = new HashSet<Integer>();
        }
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public void addUserId(Integer userId) {
        getUserIds().add(userId);
    }

    public boolean removeUserId(Integer userId) {
        return getUserIds().remove(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DuracloudGroup)) {
            return false;
        }

        DuracloudGroup that = (DuracloudGroup) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (userIds != null ? !userIds.equals(that.userIds) :
            that.userIds != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (userIds != null ? userIds.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(DuracloudGroup o) {
        return o.name.compareTo(this.name);
    }

}
