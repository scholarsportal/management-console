/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.error.DuracloudServerImageNotAvailableException;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.InstanceConfigUtilImpl;
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
    private InstanceConfigUtil instanceConfigUtil;
    private Credential rootCredential;

    public DuracloudInstanceServiceImpl(int accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr,
                                        ComputeProviderUtil computeProviderUtil)
        throws DBNotFoundException {
        this(accountId, instance, repoMgr, computeProviderUtil, null, null, null);
    }

    protected DuracloudInstanceServiceImpl(int accountId,
                                           DuracloudInstance instance,
                                           DuracloudRepoMgr repoMgr,
                                           ComputeProviderUtil computeProviderUtil,
                                           DuracloudComputeProvider computeProvider,
                                           InstanceUpdater instanceUpdater,
                                           InstanceConfigUtil instanceConfigUtil)
        throws DBNotFoundException {

        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.computeProviderUtil = computeProviderUtil;
        this.computeProvider = computeProvider;
        this.instanceUpdater = instanceUpdater;
        this.instanceConfigUtil = instanceConfigUtil;

        if (null == computeProvider) {
            initializeComputeProvider();
        }

        if (null == instanceUpdater) {
            this.instanceUpdater = new InstanceUpdaterImpl();
        }

        if (null == instanceConfigUtil) {
            this.instanceConfigUtil =
                new InstanceConfigUtilImpl(instance, repoMgr);
        }
    }

    private void initializeComputeProvider()
        throws DBNotFoundException {

        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        AccountInfo account = accountRepo.findById(instance.getAccountId());

        DuracloudComputeProviderAccountRepo providerAcctRepo =
            repoMgr.getComputeProviderAccountRepo();
        ComputeProviderAccount computeProviderAcct =
            providerAcctRepo.findById(account.getComputeProviderAccountId());

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
        restart(true);
    }

    protected void restart(boolean wait) {
        computeProvider.restart(instance.getProviderInstanceId());

        // TODO: It would be a better solution to poll the instance in order
        //       to determine when it is available to be initialized rather
        //       than just waiting 5 minutes.
        if(wait) {
            int waitMinutes = 5;
            new ThreadedInitializer(waitMinutes).start();
        } else {
            initialize();
        }
    }

    private class ThreadedInitializer extends Thread {
        private int waitMinutes;

        public ThreadedInitializer(int waitMinutes) {
            this.waitMinutes = waitMinutes;
        }

        public void run() {
            try {
                sleep(waitMinutes * 60000);
            } catch(InterruptedException e) {
            }
            initialize();
        }
    }

    @Override
    public void initialize() {
        initializeInstance();
        initializeUserRoles();
    }

    private void initializeInstance() {
        DuradminConfig duradminConfig =
            instanceConfigUtil.getDuradminConfig();
        DurastoreConfig durastoreConfig =
            instanceConfigUtil.getDurastoreConfig();
        DuraserviceConfig duraserviceConfig =
            instanceConfigUtil.getDuraserviceConfig();

        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.initializeInstance(host,
                                           duradminConfig,
                                           durastoreConfig,
                                           duraserviceConfig,
                                           restHelper);
    }

    private void initializeUserRoles() {
        try {
            DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
            Set<AccountRights> acctRights = rightsRepo.findByAccountId(accountId);

            DuracloudUserRepo userRepo = repoMgr.getUserRepo();
            Set<DuracloudUser> users = new HashSet<DuracloudUser>();
            for(AccountRights rights : acctRights) {
                DuracloudUser user = userRepo.findById(rights.getUserId());
                user.setAccountRights(rights);
                users.add(user);
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
        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.updateUserDetails(host, userBeans, restHelper);
    }

    private Credential getRootCredential() {
        if(null == rootCredential) {
            try {
                DuracloudServerImageRepo imageRepo = repoMgr.getServerImageRepo();
                ServerImage serverImage = imageRepo.findById(instance.getImageId());
                String rootPassword = serverImage.getDcRootPassword();
                rootCredential = new Credential(ServerImage.DC_ROOT_USERNAME,
                                                rootPassword);
            } catch(DBNotFoundException e) {
                throw new
                    DuracloudServerImageNotAvailableException(e.getMessage(), e);
            }
        }
        return rootCredential;
    }

}
