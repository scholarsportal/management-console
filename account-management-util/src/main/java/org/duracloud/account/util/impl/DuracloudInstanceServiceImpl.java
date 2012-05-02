/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.error.DuracloudServerImageNotAvailableException;
import org.duracloud.account.util.instance.DurabossUpdater;
import org.duracloud.account.util.instance.InstanceAccessUtil;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceInitListener;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.DurabossUpdaterImpl;
import org.duracloud.account.util.instance.impl.InstanceAccessUtilImpl;
import org.duracloud.account.util.instance.impl.InstanceConfigUtilImpl;
import org.duracloud.account.util.instance.impl.InstanceUpdaterImpl;
import org.duracloud.account.util.notification.NotificationMgrConfig;
import org.duracloud.account.util.util.AccountClusterUtil;
import org.duracloud.account.util.util.AccountUtil;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
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
    private AccountInfo accountInfo;
    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private AccountUtil accountUtil;
    private AccountClusterUtil accountClusterUtil;
    private ComputeProviderUtil computeProviderUtil;
    private DuracloudComputeProvider computeProvider;
    private InstanceUpdater instanceUpdater;
    private InstanceConfigUtil instanceConfigUtil;
    private DurabossUpdater durabossUpdater;
    private Credential rootCredential;
    private int timeoutMinutes = 20;
    private NotificationMgrConfig notMgrConfig;

    public DuracloudInstanceServiceImpl(int accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr,
                                        AccountUtil accountUtil,
                                        AccountClusterUtil accountClusterUtil,
                                        ComputeProviderUtil computeProviderUtil,
                                        NotificationMgrConfig notMgrConfig)
        throws DBNotFoundException {
        this(accountId,
             instance,
             repoMgr,
             accountUtil,
             accountClusterUtil,
             computeProviderUtil,
             null,
             null,
             null,
             null,
             notMgrConfig);
    }

    protected DuracloudInstanceServiceImpl(int accountId,
                                           DuracloudInstance instance,
                                           DuracloudRepoMgr repoMgr,
                                           AccountUtil accountUtil,
                                           AccountClusterUtil accountClusterUtil,
                                           ComputeProviderUtil computeProviderUtil,
                                           DuracloudComputeProvider computeProvider,
                                           InstanceUpdater instanceUpdater,
                                           InstanceConfigUtil instanceConfigUtil,
                                           DurabossUpdater durabossUpdater,
                                           NotificationMgrConfig notMgrConfig)
        throws DBNotFoundException {

        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.accountUtil = accountUtil;
        this.accountClusterUtil = accountClusterUtil;
        this.computeProviderUtil = computeProviderUtil;
        this.computeProvider = computeProvider;
        this.instanceUpdater = instanceUpdater;
        this.instanceConfigUtil = instanceConfigUtil;
        this.durabossUpdater = durabossUpdater;
        this.notMgrConfig = notMgrConfig;

        if (null == computeProvider) {
            initializeComputeProvider();
        }

        if (null == instanceUpdater) {
            this.instanceUpdater = new InstanceUpdaterImpl();
        }

        if (null == instanceConfigUtil) {
            this.instanceConfigUtil = new InstanceConfigUtilImpl(instance,
                                                                 repoMgr,
                                                                 accountUtil,
                                                                 notMgrConfig);
        }

        if (null == durabossUpdater){
            this.durabossUpdater = new DurabossUpdaterImpl();
        }
    }

    private void initializeComputeProvider()
        throws DBNotFoundException {
        AccountInfo account = getAccount();

        DuracloudComputeProviderAccountRepo providerAcctRepo =
            repoMgr.getComputeProviderAccountRepo();
        ServerDetails serverDetails = accountUtil.getServerDetails(account);
        ComputeProviderAccount computeProviderAcct = providerAcctRepo.findById(
            serverDetails.getComputeProviderAccountId());

        this.computeProvider = computeProviderUtil
            .getComputeProvider(computeProviderAcct.getUsername(),
                                computeProviderAcct.getPassword());
    }

    private AccountInfo getAccount() throws DBNotFoundException {
        if(null == accountInfo) {
            DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
            accountInfo = accountRepo.findById(instance.getAccountId());
        }
        return accountInfo;
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

        // Gracefully shutdown duraboss
        String host = instance.getHostName();
        DurabossConfig durabossConfig = instanceConfigUtil.getDurabossConfig();
        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());

        ServicePlan servicePlan = getServicePlan();
        try {
            durabossUpdater.stopDuraboss(host,
                                         durabossConfig,
                                         servicePlan,
                                         restHelper);
        } catch (Exception e) {
            // Do not let DuraBoss errors stop the instance shutdown.
            log.error("Error stopping DuraBoss: {}", e.getMessage(), e);
        }

        // Terminate the instance
        computeProvider.stop(instance.getProviderInstanceId());
        // Remove this instance from the DB
        repoMgr.getInstanceRepo().delete(instance.getId());
    }

    private ServicePlan getServicePlan()  {
        AccountInfo account;
        try {
            account = getAccount();
        } catch (DBNotFoundException e) {
            return null;
        }
        ServerDetails serverDetails = accountUtil.getServerDetails(account);
        return serverDetails.getServicePlan();
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
                } catch(DuraCloudRuntimeException ex) {
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
        DurabossConfig durabossConfig =
            instanceConfigUtil.getDurabossConfig();

        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.initializeInstance(host,
                                           duradminConfig,
                                           durastoreConfig,
                                           duraserviceConfig,
                                           durabossConfig,
                                           restHelper);

        ServicePlan servicePlan = getServicePlan();
        durabossUpdater.startDuraboss(host,
                                      durabossConfig,
                                      servicePlan,
                                      restHelper);
    }

    private void initializeUserRoles() {
        try {
            Set<DuracloudUser> users =
                accountClusterUtil.getAccountClusterUsers(getAccount());
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

        // collect groups for the cluster
        DuracloudGroupRepo groupRepo = repoMgr.getGroupRepo();
        Set<Integer> clusterAcctIds = getClusterAccountIds();
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
        for(int clusterAcctId : clusterAcctIds) {
            groups.addAll(groupRepo.findByAccountId(clusterAcctId));
        }

        // collect user roles for this account
        Set<SecurityUserBean> userBeans = new HashSet<SecurityUserBean>();
        for (DuracloudUser user : users) {
            String username = user.getUsername();
            String password = user.getPassword();
            String email = user.getEmail();
            Set<Role> roles = user.getRolesByAcct(accountId);

            if(roles == null) {
                roles = new HashSet<Role>();
            }

            if(roles.isEmpty()) {
                roles.add(Role.ROLE_USER);
            }

            if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
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
                SecurityUserBean bean =
                    new SecurityUserBean(username, password, grants);
                bean.setEmail(email);

                if(groups != null) {
                    for (DuracloudGroup group : groups) {
                        Set<Integer> userIds = group.getUserIds();
                        if(userIds.contains(user.getId())) {
                            bean.addGroup(group.getName());
                        }
                    }
                }

                userBeans.add(bean);
            }
        }

        // do the update
        updateUserDetails(userBeans);
    }

    private Set<Integer> getClusterAccountIds() {
        try {
            return accountClusterUtil.getClusterAccountIds(getAccount());
        } catch(DBNotFoundException e) {
            String error = "Could not retrieve account IDs for " +
                "cluster associated with account with ID " + accountId +
                " from the database due to: " + e.getMessage();
            throw new DuraCloudRuntimeException(error);
        }
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
