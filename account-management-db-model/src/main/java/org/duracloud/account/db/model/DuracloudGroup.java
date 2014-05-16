/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Erik Paulsson
 *         Date: 7/10/13
 */
@Entity
public class DuracloudGroup extends BaseEntity {

    public static final String PREFIX = "group-";
    public static final String PUBLIC_GROUP_NAME = PREFIX + "public";

    private String name;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="group_user",
        joinColumns=@JoinColumn(name="group_id", referencedColumnName="id"),
        inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="id"))
    private Set<DuracloudUser> users = new HashSet<DuracloudUser>();

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="account_id", nullable=false)
    private AccountInfo account;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DuracloudUser> getUsers() {
        return users;
    }

    public void setUsers(Set<DuracloudUser> users) {
        this.users = users;
    }

    public AccountInfo getAccount() {
        return account;
    }

    public void setAccount(AccountInfo account) {
        this.account = account;
    }
}
