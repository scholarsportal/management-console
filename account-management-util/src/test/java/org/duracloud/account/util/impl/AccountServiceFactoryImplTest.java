/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.security.AnnotationParser;
import org.duracloud.account.util.security.SecurityContextUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.annotation.Secured;

/**
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class AccountServiceFactoryImplTest {

    private AccountServiceFactoryImpl factory;

    private DuracloudRepoMgr repoMgr;
    private AccessDecisionVoter voter;
    private SecurityContextUtil securityContext;
    private DuracloudProviderAccountUtil providerAccountUtil;
    private AnnotationParser annotationParser;

    @Before
    public void setUp() throws Exception {
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        voter = EasyMock.createMock("AccessDecisionVoter",
                                    AccessDecisionVoter.class);
        securityContext = EasyMock.createMock("SecurityContextUtil",
                                              SecurityContextUtil.class);
        providerAccountUtil = EasyMock.createMock("DuracloudProviderAccountUtil",
                                                  DuracloudProviderAccountUtil.class);
        annotationParser = EasyMock.createMock("AnnotationParser",
                                               AnnotationParser.class);

        // set up securityContext
        EasyMock.expect(securityContext.getAuthentication()).andReturn(null);

        // set up annotationParser
        EasyMock.expect(annotationParser.getMethodAnnotationsForClass(Secured.class,
                                                                      AccountServiceSecuredImpl.class))
            .andReturn(null);

        factory = new AccountServiceFactoryImpl(repoMgr,
                                                voter,
                                                securityContext,
                                                providerAccountUtil,
                                                annotationParser);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(repoMgr,
                        voter,
                        securityContext,
                        providerAccountUtil,
                        annotationParser);
    }

    public void replayMocks() throws Exception {
        EasyMock.replay(repoMgr,
                        voter,
                        securityContext,
                        providerAccountUtil,
                        annotationParser);
    }

    @Test
    public void testGetAccount() throws Exception {
        int id = 3;
        AccountInfo acctInfo = createAcctInfo(id);
        replayMocks();

        // the call under test
        AccountService acctService = factory.getAccount(acctInfo);
        Assert.assertNotNull(acctService);
        Assert.assertEquals(AccountServiceSecuredImpl.class,
                            acctService.getClass());
    }

    @Test
    public void testGetAccountById() throws Exception {
        int id = 3;
        DuracloudAccountRepo acctRepo = EasyMock.createMock(
            "DuracloudAccountRepo",
            DuracloudAccountRepo.class);
        EasyMock.expect(acctRepo.findById(id)).andReturn(createAcctInfo(id));
        EasyMock.expect(repoMgr.getAccountRepo()).andReturn(acctRepo);
        EasyMock.replay(acctRepo);

        replayMocks();

        // the call under test
        AccountService acctService = factory.getAccount(id);
        Assert.assertNotNull(acctService);
        Assert.assertEquals(AccountServiceSecuredImpl.class,
                            acctService.getClass());

        EasyMock.verify(acctRepo);
    }

    private AccountInfo createAcctInfo(int id) {
        return new AccountInfo(id,
                               null,
                               null,
                               null,
                               null,
                               -1,
                               -1,
                               -1,
                               null,
                               null);
    }

}
