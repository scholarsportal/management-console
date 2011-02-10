/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.usermgmt.UserDetailsInstanceUpdater;
import org.duracloud.account.util.usermgmt.impl.UserDetailsInstanceUpdaterImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceServiceImpl implements DuracloudInstanceService {

    private Logger log = LoggerFactory.getLogger(DuracloudInstanceServiceImpl.class);

    private int accountId;
    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private ComputeProviderUtil computeProviderUtil;
    private DuracloudComputeProvider computeProvider;
    private UserDetailsInstanceUpdater userDetailsInstanceUpdater;

    public DuracloudInstanceServiceImpl(int accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr,
                                        ComputeProviderUtil computeProviderUtil)
        throws DBNotFoundException {
        this(accountId, instance, repoMgr, computeProviderUtil, null, null);
    }

    protected DuracloudInstanceServiceImpl(int accountId,
                                           DuracloudInstance instance,
                                           DuracloudRepoMgr repoMgr,
                                           ComputeProviderUtil computeProviderUtil,
                                           DuracloudComputeProvider computeProvider,
                                           UserDetailsInstanceUpdater userDetailsInstanceUpdater)
        throws DBNotFoundException {

        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.userDetailsInstanceUpdater = userDetailsInstanceUpdater;
        this.computeProviderUtil = computeProviderUtil;

        if(null != computeProvider) {
            this.computeProvider = computeProvider;
        } else {
            initializeComputeProvider();
        }

        if (null == userDetailsInstanceUpdater) {
            userDetailsInstanceUpdater = new UserDetailsInstanceUpdaterImpl();
        }
    }

    private void initializeComputeProvider()
        throws DBNotFoundException {

        DuracloudProviderAccountRepo providerAcctRepo =
            repoMgr.getProviderAccountRepo();
        ProviderAccount computeProviderAcct =
            providerAcctRepo.findById(instance.getComputeProviderAccountId());

        this.computeProvider = computeProviderUtil
            .getComputeProvider(computeProviderAcct.getUsername(),
                                computeProviderAcct.getPassword());
    }

    @Override
    public DuracloudInstance getInstanceInfo() {
        return instance;
    }

    @Override
    public String getStatus() {
        return computeProvider.getStatus(instance.getProviderInstanceId());
    }

    @Override
    public void stop() {
        computeProvider.stop(instance.getProviderInstanceId());
    }

    @Override
    public void restart() {
        computeProvider.restart(instance.getProviderInstanceId());

        // TODO: Initialize instance
    }

    @Override
    public void setUserRoles(Set<DuracloudUser> users) {
        // validate args
        if (null == users || users.size() == 0) {
            String msg = "arg users is null for instance in acct: " + accountId;
            log.warn(msg);
            throw new DuracloudInstanceUpdateException(msg);
        }

        // collect user roles for this account
        Set<SecurityUserBean> userBeans = new HashSet<SecurityUserBean>();
        for (DuracloudUser user : users) {
            String username = user.getUsername();
            String password = user.getPassword();
            Set<Role> roles = user.getRolesByAcct(accountId);

            if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) || null == roles ||
                roles.size() == 0) {
                StringBuilder msg = new StringBuilder("invalid user: ");
                msg.append(accountId + ", ");
                msg.append(username + ", ");
                msg.append(password + ", ");
                msg.append(roles);
                log.error(msg.toString());
                throw new DuracloudInstanceUpdateException(msg.toString());
            }

            List<String> grants = new ArrayList<String>();
            for (Role role : roles) {
                grants.add(role.name());
            }

            userBeans.add(new SecurityUserBean(username, password, grants));
        }

        // do the update
        updateUserDetails(userBeans);
    }

    private void updateUserDetails(Set<SecurityUserBean> userBeans) {
        Credential rootCredential = new Credential(instance.getDcRootUsername(),
                                                   instance.getDcRootPassword());
        RestHttpHelper restHelper = new RestHttpHelper(rootCredential);
        String host = instance.getHostName();

        userDetailsInstanceUpdater.updateUserDetails(host,
                                                     userBeans,
                                                     restHelper);
    }

}
