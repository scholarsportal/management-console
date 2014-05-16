/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.DuracloudInstanceServiceFactory;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.account.db.util.security.AnnotationParser;
import org.duracloud.account.db.util.security.SecurityContextUtil;
import org.duracloud.account.db.util.util.AccountClusterUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.security.error.NoUserLoggedInException;
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
    private AccountClusterUtil accountClusterUtil;
    private ComputeProviderUtil computeUtil;
    private AnnotationParser annotationParser;
    private NotificationMgr notificationMgr;

    public DuracloudInstanceServiceFactoryImpl(DuracloudRepoMgr repoMgr,
                                               AccessDecisionVoter voter,
                                               SecurityContextUtil securityContext,
                                               AccountClusterUtil accountClusterUtil,
                                               ComputeProviderUtil computeUtil,
                                               AnnotationParser annotationParser,
                                               NotificationMgr notificationMgr) {
        this.repoMgr = repoMgr;
        this.voter = voter;
        this.securityContext = securityContext;
        this.accountClusterUtil = accountClusterUtil;
        this.computeUtil = computeUtil;
        this.annotationParser = annotationParser;
        this.notificationMgr = notificationMgr;
    }

    @Override
    public DuracloudInstanceService getInstance(DuracloudInstance instance) {
        DuracloudInstanceService instanceService = new DuracloudInstanceServiceImpl(
            instance.getAccount().getId(),
            instance,
            repoMgr,
            accountClusterUtil,
            computeUtil,
            notificationMgr.getConfig());

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
