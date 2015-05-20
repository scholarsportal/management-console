/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.config.AmaEndpoint;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.InstanceType;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudGroupRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.DuracloudMillConfigService;
import org.duracloud.account.db.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.db.util.instance.DurabossUpdater;
import org.duracloud.account.db.util.instance.InstanceAccessUtil;
import org.duracloud.account.db.util.instance.InstanceConfigUtil;
import org.duracloud.account.db.util.instance.InstanceInitListener;
import org.duracloud.account.db.util.instance.InstanceUpdater;
import org.duracloud.account.db.util.instance.impl.DurabossUpdaterImpl;
import org.duracloud.account.db.util.instance.impl.InstanceAccessUtilImpl;
import org.duracloud.account.db.util.instance.impl.InstanceConfigUtilImpl;
import org.duracloud.account.db.util.instance.impl.InstanceUpdaterImpl;
import org.duracloud.account.db.util.notification.NotificationMgrConfig;
import org.duracloud.account.db.util.util.UserFinderUtil;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceServiceImpl implements DuracloudInstanceService,
                                                     InstanceInitListener {

    private Logger log = LoggerFactory.getLogger(DuracloudInstanceServiceImpl.class);

    private static final int MAX_INIT_RETRIES = 10;

    private Long accountId;
    private AccountInfo accountInfo;
    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private UserFinderUtil userFinderUtil;
    private ComputeProviderUtil computeProviderUtil;
    private DuracloudComputeProvider computeProvider;
    private InstanceUpdater instanceUpdater;
    private InstanceConfigUtil instanceConfigUtil;
    private DurabossUpdater durabossUpdater;
    private Credential rootCredential;
    private NotificationMgrConfig notMgrConfig;
    private DuracloudMillConfigService duracloudMillService;
    private int timeoutMinutes = 20;

    public DuracloudInstanceServiceImpl(Long accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr,
                                        UserFinderUtil userFinderUtil,
                                        ComputeProviderUtil computeProviderUtil,
                                        NotificationMgrConfig notMgrConfig,
                                        AmaEndpoint amaEndpoint,
                                        DuracloudMillConfigService duracloudMillService) {
        this(accountId,
                instance,
                repoMgr,
                userFinderUtil,
                computeProviderUtil,
                null,
                null,
                null,
                null,
                notMgrConfig, 
                amaEndpoint,
                duracloudMillService);
    }

    protected DuracloudInstanceServiceImpl(Long accountId,
                                           DuracloudInstance instance,
                                           DuracloudRepoMgr repoMgr,
                                           UserFinderUtil accountClusterUtil,
                                           ComputeProviderUtil computeProviderUtil,
                                           DuracloudComputeProvider computeProvider,
                                           InstanceUpdater instanceUpdater,
                                           InstanceConfigUtil instanceConfigUtil,
                                           DurabossUpdater durabossUpdater,
                                           NotificationMgrConfig notMgrConfig,
                                           AmaEndpoint amaEndpoint,
                                           DuracloudMillConfigService duracloudMillService) {

        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.userFinderUtil = accountClusterUtil;
        this.computeProviderUtil = computeProviderUtil;
        this.computeProvider = computeProvider;
        this.instanceUpdater = instanceUpdater;
        this.instanceConfigUtil = instanceConfigUtil;
        this.durabossUpdater = durabossUpdater;
        this.notMgrConfig = notMgrConfig;
        this.duracloudMillService = duracloudMillService;
        
        if (null == computeProvider) {
            initializeComputeProvider();
        }

        if (null == instanceUpdater) {
            this.instanceUpdater = new InstanceUpdaterImpl();
        }

        if (null == instanceConfigUtil) {
            this.instanceConfigUtil = new InstanceConfigUtilImpl(instance,
                    repoMgr,
                    notMgrConfig,
                    amaEndpoint,
                    duracloudMillService);
        }

        if (null == durabossUpdater){
            this.durabossUpdater = new DurabossUpdaterImpl();
        }
    }

    @Override
    public Long getAccountId() {
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

    @Override
    public InstanceType getInstanceType()
            throws DuracloudInstanceNotAvailableException {
        return computeProvider.getInstanceType(instance.getProviderInstanceId());
    }

    @Override
    public void stop() {
        log.info("Stopping instance with provider ID {} at host {}",
                instance.getProviderInstanceId(), instance.getHostName());

        // Gracefully shutdown duraboss
        String host = instance.getHostName();
        DurabossConfig durabossConfig = instanceConfigUtil.getDurabossConfig();
        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());

        try {
            durabossUpdater.stopDuraboss(host,
                                         durabossConfig,
                                         restHelper);
        } catch (Exception e) {
            // Do not let DuraBoss errors stop the instance shutdown.
            log.error("Error stopping DuraBoss: {}", e.getMessage(), e);
        }

        // Terminate the instance
        computeProvider.stop(instance.getProviderInstanceId());
        // Remove this instance from the DB
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        AccountInfo account = accountRepo.getOne(instance.getAccount().getId());
        account.setInstance(null);
        accountRepo.saveAndFlush(account);
        repoMgr.getInstanceRepo().delete(instance.getId());
    }

    @Override
    public void initialize() {
        log.info("Initializing instance for account {} at host {}",
                accountId, instance.getHostName());

        doInitialize(true);
    }

    @Override
    public void reInitialize() {
        log.info("Re-Initializing instance for account {} at host {}",
                accountId, instance.getHostName());

        doInitialize(false);
    }

    @Override
    public void reInitializeUserRoles() {
        initializeUserRoles();
    }

    @Override
    public void restart() {
        log.info("Restarting instance with provider ID {} at host {}",
                instance.getProviderInstanceId(), instance.getHostName());

        computeProvider.restart(instance.getProviderInstanceId());
        doInitialize(true);
    }

    @Override
    public void handleInstanceInitFailure() {
        // Simply log exception for now. In the future, it may be useful to
        // capture the error so that it can be presented further up the chain.
        log.error(
                "Failure attempting to initialize instance " + instance.getId() +
                        " for account " + accountId);
    }

    protected void doInitialize(boolean wait) {
        if(wait && timeoutMinutes > 0) {
            new ThreadedInitializer(timeoutMinutes, this).start();
        } else {
            initializeUserRoles();
            initializeInstance();
            updateInstance();
        }
    }

    private void initializeInstance() {
        DuradminConfig duradminConfig =
                instanceConfigUtil.getDuradminConfig();
        DurastoreConfig durastoreConfig =
            instanceConfigUtil.getDurastoreConfig();
        DurabossConfig durabossConfig =
                instanceConfigUtil.getDurabossConfig();

        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.initializeInstance(host,
                                           duradminConfig,
                                           durastoreConfig,
                                           durabossConfig,
                                           restHelper);

        durabossUpdater.startDuraboss(host,
                                      durabossConfig,
                                      restHelper);
    }

    private void updateInstance() {
        DuracloudInstance update =
                repoMgr.getInstanceRepo().findOne(instance.getId());
        update.setInitialized(true);

        repoMgr.getInstanceRepo().save(update);
    }

    private void initializeUserRoles() {
        Set< DuracloudUser > users =
                userFinderUtil.getAccountUsers(getAccount());
        setUserRoles(users);
    }

    @Override
    public void setUserRoles(Set<DuracloudUser> users) {
        log.info("Initializing user roles for account {} at host {}",
                accountId, instance.getHostName());

        // validate args
        if (null == users) {
            String msg = "arg users is null for instance in acct: " + accountId;
            log.warn(msg);
            throw new DuracloudInstanceUpdateException(msg);
        }

        // collect groups for the cluster
        DuracloudGroupRepo groupRepo = repoMgr.getGroupRepo();
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
            groups.addAll(groupRepo.findByAccountId(accountId));

        // collect user roles for this account
        Set<SecurityUserBean> userBeans = new HashSet<SecurityUserBean>();
        for (DuracloudUser user : users) {
            String username = user.getUsername();
            String password = user.getPassword();
            String email = user.getEmail();
            String ipLimits = annotateAddressRange(user.getAllowableIPAddressRange());
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
            for (Role role : roles) {
                grants.add(role.name());
            }

            if(!user.isRoot()) { // Do not include root users in user list for instance
                SecurityUserBean bean =
                        new SecurityUserBean(username, password, grants);
                bean.setEmail(email);
                bean.setIpLimits(ipLimits);

                if(groups != null) {
                    for (DuracloudGroup group : groups) {
                        Set<DuracloudUser> grpUsers = group.getUsers();
                        if(grpUsers.contains(user)) {
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

    /**
     * For a user account with an IP limitation, this method is used to update
     * the list of allowed IPs to include the IP of the DuraCloud instance itself.
     * This is required to allow the calls made between applications (like those
     * made from DurAdmin to DuraStore) to pass through the IP range check.
     *
     * @param baseRange set of IP ranges set by the user
     * @return baseRange plus the instance elastic IP, or null if baseRange is null
     */
    private String annotateAddressRange(String baseRange) {
        if(null == baseRange || baseRange.equals("")) {
            return baseRange;
        } else {
            String elasticIp = getAccount().getServerDetails()
                                           .getComputeProviderAccount().getElasticIp();
            String delimeter = ";";
            return baseRange + delimeter + elasticIp + "/32";
        }
    }



    private void updateUserDetails(Set<SecurityUserBean> userBeans) {
        RestHttpHelper restHelper = new RestHttpHelper(getRootCredential());
        String host = instance.getHostName();

        instanceUpdater.updateUserDetails(host, userBeans, restHelper);
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

    private AccountInfo getAccount() {
        if(null == accountInfo) {
            accountInfo = repoMgr.getAccountRepo().findOne(accountId);
        }
        return accountInfo;
    }

    private ServerImage getServerImage() {
        return repoMgr.getServerImageRepo().findOne(instance.getImage().getId());
    }

    private String getStatusFromComputeProvider() throws DuracloudInstanceNotAvailableException{
        return computeProvider.getStatus(instance.getProviderInstanceId());
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

    private void initializeComputeProvider() {
        AccountInfo account = getAccount();
        ComputeProviderAccount computeProviderAccount =
                account.getServerDetails().getComputeProviderAccount();
        computeProvider = computeProviderUtil.getComputeProvider(
                computeProviderAccount.getUsername(),
                computeProviderAccount.getPassword());
    }
}
