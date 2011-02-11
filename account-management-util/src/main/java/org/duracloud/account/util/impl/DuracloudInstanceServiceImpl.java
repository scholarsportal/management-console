/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.InstanceUpdaterImpl;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
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
    private InstanceUpdater instanceUpdater;

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
                                           InstanceUpdater instanceUpdater)
        throws DBNotFoundException {

        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.instanceUpdater = instanceUpdater;
        this.computeProviderUtil = computeProviderUtil;

        if(null != computeProvider) {
            this.computeProvider = computeProvider;
        } else {
            initializeComputeProvider();
        }

        if (null == instanceUpdater) {
            this.instanceUpdater = new InstanceUpdaterImpl();
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
        // TODO: Should likely be a wait in here
        initialize();
    }

    @Override
    public void initialize() {
        initializeInstance();
        initializeUserRoles();
    }

    private void initializeInstance() {
        DuradminConfig duradminConfig = getDuradminConfig();
        DurastoreConfig durastoreConfig = getDurastoreConfig();
        DuraserviceConfig duraserviceConfig = getDuraserviceConfig();

        Credential rootCredential = new Credential(instance.getDcRootUsername(),
                                                   instance.getDcRootPassword());
        RestHttpHelper restHelper = new RestHttpHelper(rootCredential);
        String host = instance.getHostName();

        instanceUpdater.initializeInstance(host,
                                           duradminConfig,
                                           durastoreConfig,
                                           duraserviceConfig,
                                           restHelper);
    }

    private DuradminConfig getDuradminConfig() {
        DuradminConfig config = new DuradminConfig();
        config.setDurastoreHost(instance.getHostName());
        config.setDurastorePort("443");
        config.setDurastoreContext(DurastoreConfig.QUALIFIER);
        config.setDuraserviceHost(instance.getHostName());
        config.setDuraservicePort("443");
        config.setDuraserviceContext(DuraserviceConfig.QUALIFIER);
        return config;
    }

    private DurastoreConfig getDurastoreConfig() {
        DurastoreConfig config = new DurastoreConfig();
        // TODO: fill out (after adding setters to DurastoreConfig)
        return config;
    }

    private DuraserviceConfig getDuraserviceConfig() {
        DuraserviceConfig config = new DuraserviceConfig();
        // TODO: fill out (after adding setters to DuraserviceConfig)
        return config;
    }    

    private void initializeUserRoles() {
        try {
            DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
            Set<AccountRights> acctRights = rightsRepo.findByAccountId(accountId);

            DuracloudUserRepo userRepo = repoMgr.getUserRepo();
            Set<DuracloudUser> users = new HashSet<DuracloudUser>();
            for(AccountRights right : acctRights) {
                users.add(userRepo.findById(right.getUserId()));
            }
            setUserRoles(users);
        } catch(DBNotFoundException e) {
            String msg = "Exception encountered attempting to initialize user" +
                         " roles on instance for account " +
                         accountId + ": " + e.getMessage();
            throw new DuracloudInstanceUpdateException(msg);
        }
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

        instanceUpdater.updateUserDetails(host, userBeans, restHelper);
    }

}
