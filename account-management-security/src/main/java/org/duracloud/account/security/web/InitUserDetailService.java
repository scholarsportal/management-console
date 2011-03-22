/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InitUserCredential;
import org.duracloud.account.common.domain.Role;
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

        DuracloudUser initUser = new DuracloudUser(-1,
                                                   init.getUsername(),
                                                   init.getInitEncodedPassword(),
                                                   "Init",
                                                   "User",
                                                   "none@none.org");


        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_INIT);

        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        rightsSet.add(new AccountRights(-1, -1, -1, roles));

        initUser.setAccountRights(rightsSet);

        return initUser;
    }
}
