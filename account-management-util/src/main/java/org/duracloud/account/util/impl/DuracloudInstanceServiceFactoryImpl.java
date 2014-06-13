/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AmaConfigMgr;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudInstanceServiceFactory;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.security.AnnotationParser;
import org.duracloud.account.util.security.SecurityContextUtil;
import org.duracloud.account.util.util.AccountClusterUtil;
import org.duracloud.account.util.util.AccountUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.error.NoUserLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.core.Authentication;

/**
 * @author Andrew Woods
 *         Date: 4/10/11
 */
public class DuracloudInstanceServiceFactoryImpl implements DuracloudInstanceServiceFactory {

    private Logger log = LoggerFactory.getLogger(
        DuracloudInstanceServiceFactoryImpl.class);

    private DuracloudRepoMgr repoMgr;
    private AccessDecisionVoter voter;
    private SecurityContextUtil securityContext;
    private AccountUtil accountUtil;
    private AccountClusterUtil accountClusterUtil;
    private ComputeProviderUtil computeUtil;
    private AnnotationParser annotationParser;
    private NotificationMgr notificationMgr;
    private AmaConfigMgr amaConfigMgr;
    
    public DuracloudInstanceServiceFactoryImpl(DuracloudRepoMgr repoMgr,
                                               AccessDecisionVoter voter,
                                               SecurityContextUtil securityContext,
                                               AccountUtil accountUtil,
                                               AccountClusterUtil accountClusterUtil,
                                               ComputeProviderUtil computeUtil,
                                               AnnotationParser annotationParser,
                                               NotificationMgr notificationMgr,
                                               AmaConfigMgr amaConfigMgr) {
        this.repoMgr = repoMgr;
        this.voter = voter;
        this.securityContext = securityContext;
        this.accountUtil = accountUtil;
        this.accountClusterUtil = accountClusterUtil;
        this.computeUtil = computeUtil;
        this.annotationParser = annotationParser;
        this.notificationMgr = notificationMgr;
        this.amaConfigMgr = amaConfigMgr;
    }

    @Override
    public DuracloudInstanceService getInstance(DuracloudInstance instance)
        throws DBNotFoundException {
        DuracloudInstanceService instanceService = new DuracloudInstanceServiceImpl(
            instance.getAccountId(),
            instance,
            repoMgr,
            accountUtil,
            accountClusterUtil,
            computeUtil,
            notificationMgr.getConfig(),
            amaConfigMgr.getConfig());

        Authentication authentication = getAuthentication();
        return new DuracloudInstanceServiceSecuredImpl(instanceService,
                                                       authentication,
                                                       voter,
                                                       annotationParser);
    }

    private Authentication getAuthentication() {
        try {
            return securityContext.getAuthentication();

        } catch (NoUserLoggedInException e) {
            log.warn("No user found in security context.");
            throw new DuraCloudRuntimeException(e);
        }
    }
}
