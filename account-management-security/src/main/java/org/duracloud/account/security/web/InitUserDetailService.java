/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.model.util.InitUserCredential;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Jan 31, 2011
 */
public class InitUserDetailService implements UserDetailsService {

    private InitUserCredential init = new InitUserCredential();

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException, DataAccessException {
        if (null == username || !username.equals(init.getUsername())) {
            throw new UsernameNotFoundException(username + " not found");
        }

        DuracloudUser initUser = new DuracloudUser();
        initUser.setUsername(init.getUsername());
        initUser.setPassword(init.getInitEncodedPassword());
        initUser.setFirstName("Init");
        initUser.setLastName("User");
        initUser.setEmail("none@none.org");
        initUser.setSecurityQuestion("question");
        initUser.setSecurityAnswer("answer");

        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_INIT);

        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        AccountRights accountRights = new AccountRights();
        accountRights.setRoles(roles);
        rightsSet.add(accountRights);

        initUser.setAccountRights(rightsSet);

        return initUser;
    }
}
