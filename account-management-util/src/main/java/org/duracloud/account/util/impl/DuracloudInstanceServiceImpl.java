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
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
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
import org.duracloud.account.util.instance.InstanceAccessUtil;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceInitListener;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.InstanceAccessUtilImpl;
import org.duracloud.account.util.instance.impl.InstanceConfigUtilImpl;
import org.duracloud.account.util.instance.impl.InstanceUpdaterImpl;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DurareportConfig;
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
public class DuracloudInstanceServiceImpl implements DuracloudInstanceService,
                                                     InstanceInitListener {

    private Logger log =
        LoggerFactory.getLogger(DuracloudInstanceServiceImpl.class);

    private static final int MAX_INIT_RETRIES = 10;

    private int accountId;
    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private ComputeProviderUtil computeProviderUtil;
    private DuracloudComputeProvider computeProvider;
    private InstanceUpdater instanceUpdater;
    private InstanceConfigUtil instanceConfigUtil;
    private Credential rootCredential;
    private int timeoutMinutes = 20;

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
    public int getAccountId() {
        return accountId;
    }

    @Override
    public DuracloudInstance getInstanceInfo() {
        return instance;
    }

    @Override
    public String getInstanceVersion() {
        ServerImage serverImage = getServerImage();
        return serverImage.getVersion();
    }

    @Override
    public String getStatus() throws DuracloudInstanceNotAvailableException {
        return getStatusFromComputeProvider();
    }

    @Override
    public String getStatusInternal() throws DuracloudInstanceNotAvailableException {
        return getStatusFromComputeProvider();
    }

    private String getStatusFromComputeProvider() throws DuracloudInstanceNotAvailableException{
        return computeProvider.getStatus(instance.getProviderInstanceId());
    }
    
    @Override
    public void stop() {
        log.info("Stopping instance with provider ID {} at host {}",
                 instance.getProviderInstanceId(), instance.getHostName());

        // Terminate the instance
        computeProvider.stop(instance.getProviderInstanceId());
        // Remove this instance from the DB
        repoMgr.getInstanceRepo().delete(instance.getId());
    }

    @Override
    public void restart() {
        log.info("Restarting instance with provider ID {} at host {}",
                 instance.getProviderInstanceId(), instance.getHostName());

        computeProvider.restart(instance.getProviderInstanceId());
        doInitialize(true);
    }

    public void handleInstanceInitFailure() {
        // Simply log exception for now. In the future, it may be useful to
        // capture the error so that it can be presented further up the chain.
        log.error(
            "Failure attempting to initialize instance " + instance.getId() +
                " for account " + accountId);
    }

    private class ThreadedInitializer extends Thread {
        private int timeoutMinutes;
        private InstanceInitListener listener;

        public ThreadedInitializer(int timeoutMinutes,
                                   InstanceInitListener listener) {
            this.timeoutMinutes = timeoutMinutes;
            this.listener = listener;
        }

        public void run() {
            try {
                InstanceAccessUtil accessUtil = new InstanceAccessUtilImpl();
                accessUtil.waitInstanceAvailable(instance.getHostName(),
                                                 timeoutMinutes * 60000);
            } catch(Exception e) {
                logInitException(e);
            }
            retryInitialize();
        }

        public void retryInitialize() {
            for(int i=0; i < MAX_INIT_RETRIES; i++) {
                wait(i * 60000);
                try {
                    doInitialize(false);
                    return;
                } catch(DuracloudInstanceUpdateException ex) {
                    logInitException(ex);
                }
            }
            listener.handleInstanceInitFailure();
        }

        private void logInitException(Exception e) {
            log.warn("Exception encountered attempting to initialize instance: " +
                     e.getMessage(), e);
        }

        private void wait(int milliseconds) {
            try {
                sleep(milliseconds);
            } catch(InterruptedException e) {
            }
        }
    }

    @Override
    public void initialize() {
        log.info("Initializing instance for account {} at host {}",
                 accountId, instance.getHostName());

        doInitialize(true);
    }

    @Override
    public void reInitializeUserRoles() {
        initializeUserRoles();
    }

    @Override
    public void reInitialize() {
        log.info("Re-Initializing instance for account {} at host {}",
                 accountId, instance.getHostName());

        doInitialize(false);
    }

    protected void doInitialize(boolean wait) {
        if(wait && timeoutMinutes > 0) {
            new ThreadedInitializer(timeoutMinutes, this).start();
        } else {
            initializeInstance();
            initializeUserRoles();
            updateInstance();
        }
    }

    /*
     * Sets the number of minutes to wait for an instance to be available.
     * This method is intended to be used only for testing purposes.
     */
    protected void setInitializeTimeout(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    private void initializeInstance() {
        DuradminConfig duradminConfig =
            instanceConfigUtil.getDuradminConfig();
        DurastoreConfig durastoreConfig =
            instanceConfigUtil.getDurastoreConfig();
        DuraserviceConfig duraserviceConfig =
            instanceConfigUtil.getDuraserviceConfig();
        DurareportConfig durareportConfig =
            instanceConfigUtil.getDurareportConfig();

        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.initializeInstance(host,
                                           duradminConfig,
                                           durastoreConfig,
                                           duraserviceConfig,
                                           durareportConfig,
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
        log.info("Initializing user roles for account {} at host {}",
                 accountId, instance.getHostName());

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
            boolean root = false;
            for (Role role : roles) {
                grants.add(role.name());

                if(role.equals(Role.ROLE_ROOT)) {
                    root = true;
                }
            }

            if(!root) { // Do not include root users in user list for instance
                userBeans.add(new SecurityUserBean(username, password, grants));
            }
        }

        // do the update
        updateUserDetails(userBeans);
    }

    private void updateUserDetails(Set<SecurityUserBean> userBeans) {
        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.updateUserDetails(host, userBeans, restHelper);
    }

    private void updateInstance() {
        try {
            DuracloudInstance update =
                repoMgr.getInstanceRepo().findById(instance.getId());
            update.setInitialized(true);

            repoMgr.getInstanceRepo().save(update);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }

    private Credential getRootCredential() {
        if(null == rootCredential) {
            ServerImage serverImage = getServerImage();
            String rootPassword = serverImage.getDcRootPassword();
            rootCredential = new Credential(ServerImage.DC_ROOT_USERNAME,
                                            rootPassword);
        }
        return rootCredential;
    }

    private ServerImage getServerImage() {
        try {
            DuracloudServerImageRepo imageRepo = repoMgr.getServerImageRepo();
            return imageRepo.findById(instance.getImageId());
        } catch(DBNotFoundException e) {
            throw new
                DuracloudServerImageNotAvailableException(e.getMessage(), e);
        }
    }    

}
