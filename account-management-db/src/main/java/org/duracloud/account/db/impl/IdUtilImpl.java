/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * @author: Bill Branan
 *          Date: Dec 3, 2010
 */
public class IdUtilImpl implements IdUtil {

    private final Logger log = LoggerFactory.getLogger(IdUtilImpl.class);

    private String host;
    private String port;
    private String context;
    private RestHttpHelper restHelper;

    private int accountId = -1;
    private int userInvitationId = -1;
    private int instanceId = -1;
    private int serverImageId = -1;
    private int computeProviderAccountId = -1;
    private int storageProviderAccountId = -1;
    private int serviceRepositoryId = -1;
    private int serverDetailsId = -1;
    private int accountClusterId = -1;

    public void initialize(String host,
                           String port,
                           String context,
                           String username,
                           String password,
                           DuracloudAccountRepo accountRepo,
                           DuracloudUserInvitationRepo userInvitationRepo,
                           DuracloudInstanceRepo instanceRepo,
                           DuracloudServerImageRepo serverImageRepo,
                           DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                           DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                           DuracloudServiceRepositoryRepo serviceRepositoryRepo,
                           DuracloudServerDetailsRepo serverDetailsRepo,
                           DuracloudAccountClusterRepo accountClusterRepo) {
        initialize(host,
                   port,
                   context,
                   new RestHttpHelper(new Credential(username, password)),
                   accountRepo,
                   userInvitationRepo,
                   instanceRepo,
                   serverImageRepo,
                   computeProviderAccountRepo,
                   storageProviderAccountRepo,
                   serviceRepositoryRepo,
                   serverDetailsRepo,
                   accountClusterRepo);
    }

    // For unit-test
    protected void initialize(String host,
                              String port,
                              String context,
                              RestHttpHelper restHelper,
                              DuracloudAccountRepo accountRepo,
                              DuracloudUserInvitationRepo userInvitationRepo,
                              DuracloudInstanceRepo instanceRepo,
                              DuracloudServerImageRepo serverImageRepo,
                              DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                              DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                              DuracloudServiceRepositoryRepo serviceRepositoryRepo,
                              DuracloudServerDetailsRepo serverDetailsRepo,
                              DuracloudAccountClusterRepo accountClusterRepo) {
        if (null == host || null == port || null == context) {
            throw new IllegalArgumentException("Args must not be null!");
        }

        this.host = host;
        this.port = port;
        this.context = context;

        if (null == restHelper) {
            restHelper = new RestHttpHelper();
        }
        this.restHelper = restHelper;

        this.accountId = max(accountRepo.getIds());
        this.userInvitationId = max(userInvitationRepo.getIds());
        this.instanceId = max(instanceRepo.getIds());
        this.serverImageId = max(serverImageRepo.getIds());
        this.computeProviderAccountId = max(computeProviderAccountRepo.getIds());
        this.storageProviderAccountId = max(storageProviderAccountRepo.getIds());
        this.serviceRepositoryId = max(serviceRepositoryRepo.getIds());
        this.serverDetailsId = max(serverDetailsRepo.getIds());
        this.accountClusterId = max(accountClusterRepo.getIds());
    }

    private int max(Collection<? extends Integer> c) {
        // this check is necessary because Collections.max(int)
        // throws a NoSuchElementException when the collection
        // is empty.
        return c.isEmpty() ? 0 : Collections.max(c);
    }

    private void checkInitialized() {
        if (null == host || null == port || null == context ||
                null == restHelper || accountId < 0 || userInvitationId < 0) {
            throw new DBUninitializedException("IdUtil must be initialized");
        }
    }

    @Override
    public synchronized int newAccountId() {
        checkInitialized();
        return ++accountId;
    }

    @Override
    public synchronized int newUserInvitationId() {
        checkInitialized();
        return ++userInvitationId;
    }

    @Override
    public synchronized int newInstanceId() {
        checkInitialized();
        return ++instanceId;
    }

    @Override
    public int newServerImageId() {
        checkInitialized();
        return ++serverImageId;
    }

    @Override
    public int newComputeProviderAccountId() {
        checkInitialized();
        return ++computeProviderAccountId;
    }

    @Override
    public int newStorageProviderAccountId() {
        checkInitialized();
        return ++storageProviderAccountId;
    }

    @Override
    public int newServiceRepositoryId() {
        checkInitialized();
        return ++serviceRepositoryId;
    }

    @Override
    public int newServerDetailsId() {
        checkInitialized();
        return ++serverDetailsId;
    }

    @Override
    public int newAccountClusterId() {
        checkInitialized();
        return ++accountClusterId;
    }

    @Override
    public int newUserId() {
        return doGetId("user");
    }

    @Override
    public int newRightsId() {
        return doGetId("rights");
    }

    @Override
    public int newGroupId() {
        return doGetId("group");
    }

    private synchronized int doGetId(String resource) {
        checkInitialized();

        RestHttpHelper.HttpResponse response;
        try {
            response = restHelper.post(getBaseUrl() + "/" + resource,
                                       null,
                                       null);
        } catch (Exception e) {
            log.error("Error getting new '" + resource + "' ID!", e);
            throw new DuraCloudRuntimeException(
                    "Error getting '" + resource + "' ID: msg = " + e.getMessage(),
                    e);
        }

        String body;
        try {
            body = response.getResponseBody();

        } catch (Exception e) {
            log.error("Error getting response body for '" + resource + "' ID!",
                      e);
            throw new DuraCloudRuntimeException(
                    "Error getting response body for '" + resource + "' ID: msg = " + e
                            .getMessage(),
                    e);
        }

        if (null == body) {
            log.error("Response was null for new '{}' ID!", resource);
        }

        try {
            return Integer.parseInt(body);

        } catch (Exception e) {
            log.error("Error parsing integer from new '" + resource + "' ID body: {}",
                      body,
                      e);
            throw new DuraCloudRuntimeException(
                    "Error parsing integer from new '" + resource + "' ID body: " + body,
                    e);
        }
    }

    private String getBaseUrl() {
        return getProtocol() + getHost() + ":" + getPort() + "/" + getContext() + "/id";
    }

    private String getProtocol() {
        String protocol = "http://";
        if (getPort().equals("443")) {
            protocol = "https://";
        }
        return protocol;
    }

    private String getHost() {
        return host;
    }

    private String getPort() {
        return port;
    }

    private String getContext() {
        return context;
    }

}
