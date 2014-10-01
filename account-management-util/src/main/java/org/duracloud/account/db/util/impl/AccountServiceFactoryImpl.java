/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AmaEndpoint;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.AccountServiceFactory;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.security.AnnotationParser;
import org.duracloud.account.db.util.security.SecurityContextUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.error.NoUserLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.core.Authentication;

/**
 * This class creates security-wrapped instances of AccountService.
 *
 * @author Andrew Woods
 *         Date: 4/7/11
 */
public class AccountServiceFactoryImpl implements AccountServiceFactory {

    private Logger log = LoggerFactory.getLogger(AccountServiceFactoryImpl.class);

    private DuracloudRepoMgr repoMgr;
    private AccessDecisionVoter voter;
    private SecurityContextUtil securityContext;
    private AnnotationParser annotationParser;
    private AmaEndpoint amaEndpoint;

    public AccountServiceFactoryImpl(DuracloudRepoMgr repoMgr,
                                     AccessDecisionVoter voter,
                                     SecurityContextUtil securityContext,
                                     AnnotationParser annotationParser,
                                     AmaEndpoint amaEndpoint) {
        this.repoMgr = repoMgr;
        this.voter = voter;
        this.securityContext = securityContext;
        this.annotationParser = annotationParser;
        this.amaEndpoint = amaEndpoint;
    }

    @Override
    public AccountService getAccount(Long acctId)
        throws AccountNotFoundException {
        AccountInfo acctInfo = repoMgr.getAccountRepo().findOne(acctId);
        return getAccount(acctInfo);
}

    @Override
    public AccountService getAccount(AccountInfo acctInfo) {
        AccountService acctService = new AccountServiceImpl(amaEndpoint, acctInfo,
                                                            repoMgr);

        Authentication authentication = getAuthentication();
        return new AccountServiceSecuredImpl(acctService,
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
