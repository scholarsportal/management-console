/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Erik Paulsson
 *         Date: 7/10/13
 */
@Entity
public class AccountRights extends BaseEntity {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="account_id", nullable=false)
    private AccountInfo account;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private DuracloudUser user;

    @ElementCollection(targetClass=Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="account_rights_role",
                     joinColumns=@JoinColumn(name="account_rights_id"))
    @Column(name="role")
    private Set<Role> roles;

    public AccountInfo getAccount() {
        return account;
    }

    public void setAccount(AccountInfo account) {
        this.account = account;
    }

    public DuracloudUser getUser() {
        return user;
    }

    public void setUser(DuracloudUser user) {
        this.user = user;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
