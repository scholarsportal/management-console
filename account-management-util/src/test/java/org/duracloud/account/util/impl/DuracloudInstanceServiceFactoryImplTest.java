/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.util.DuracloudInstanceService;
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
 *         Date: 4/11/11
 */
public class DuracloudInstanceServiceFactoryImplTest extends DuracloudInstanceServiceTestBase {

    private DuracloudInstanceServiceFactoryImpl factory;

    private AccessDecisionVoter voter;
    private SecurityContextUtil securityContext;
    private AnnotationParser annotationParser;


    @Before
    public void setUp() throws Exception {
        super.setUpInitComputeProvider();

        voter = EasyMock.createMock("AccessDecisionVoter",
                                    AccessDecisionVoter.class);
        securityContext = EasyMock.createMock("SecurityContextUtil",
                                              SecurityContextUtil.class);
        annotationParser = EasyMock.createMock("AnnotationParser",
                                               AnnotationParser.class);

        // set up securityContext
        EasyMock.expect(securityContext.getAuthentication()).andReturn(null);

        // set up annotationParser
        EasyMock.expect(annotationParser.getMethodAnnotationsForClass(Secured.class,
                                                                      DuracloudInstanceServiceSecuredImpl.class))
            .andReturn(null);

        factory = new DuracloudInstanceServiceFactoryImpl(repoMgr,
                                                          voter,
                                                          securityContext,
                                                          computeProviderUtil,
                                                          annotationParser);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(repoMgr,
                        voter,
                        securityContext,
                        computeProviderUtil,
                        annotationParser);
    }

    @Override
    public void replayMocks() {
        super.replayMocks();
        EasyMock.replay(voter, securityContext, annotationParser);
    }

    @Test
    public void testGetInstance() throws Exception {
        int acctId = 3;
        DuracloudInstance instance = createInstance(acctId);
        replayMocks();

        // the call under test
        DuracloudInstanceService instanceService = factory.getInstance(instance);
        Assert.assertNotNull(instanceService);
        Assert.assertEquals(DuracloudInstanceServiceSecuredImpl.class,
                            instanceService.getClass());
    }

    private DuracloudInstance createInstance(int acctId) {
        int id = -1;
        int imageId = -1;
        int accountId = acctId;
        String hostName = "host-name";
        String providerInstanceId = "provider-id";

        return new DuracloudInstance(id,
                                     imageId,
                                     accountId,
                                     hostName,
                                     providerInstanceId);
    }

}
