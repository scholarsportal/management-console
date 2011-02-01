/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.security.web;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.security.domain.InitUserCredential;
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
                                                   init.getPassword(),
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
