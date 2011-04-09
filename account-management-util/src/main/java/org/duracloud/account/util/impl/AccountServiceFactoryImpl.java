/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.AccountServiceFactory;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.security.AnnotationParser;
import org.duracloud.account.util.security.SecurityContextUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.security.error.NoUserLoggedInException;
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
    private DuracloudProviderAccountUtil providerAccountUtil;
    private AnnotationParser annotationParser;

    public AccountServiceFactoryImpl(DuracloudRepoMgr repoMgr,
                                     AccessDecisionVoter voter,
                                     SecurityContextUtil securityContext,
                                     DuracloudProviderAccountUtil providerAccountUtil,
                                     AnnotationParser annotationParser) {
        this.repoMgr = repoMgr;
        this.voter = voter;
        this.securityContext = securityContext;
        this.providerAccountUtil = providerAccountUtil;
        this.annotationParser = annotationParser;
    }

    @Override
    public AccountService getAccount(int acctId)
        throws AccountNotFoundException {
        try {
            AccountInfo acctInfo = repoMgr.getAccountRepo().findById(acctId);
            return getAccount(acctInfo);

        } catch (DBNotFoundException e) {
            throw new AccountNotFoundException(acctId);
        }
    }

    @Override
    public AccountService getAccount(AccountInfo acctInfo) {
        AccountService acctService = new AccountServiceImpl(acctInfo,
                                                            repoMgr,
                                                            providerAccountUtil);

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
