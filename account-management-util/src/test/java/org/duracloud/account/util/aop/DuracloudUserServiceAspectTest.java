/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.aop;

import org.aspectj.lang.JoinPoint;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
public class DuracloudUserServiceAspectTest {

    private DuracloudUserServiceAspect aspect;

    private UserDetailsPropagator propagator;
    private JoinPoint joinPoint;

    private int userId = 7;
    private int acctId = 9;
    private Role role = Role.ROLE_ADMIN;

    @After
    public void tearDown() {
        EasyMock.verify(propagator);
        EasyMock.verify(joinPoint);
    }

    @Test
    public void testSetUserRightsFalse() throws Exception {
        boolean success = false;
        doTestSetUserRights(success);
    }

    @Test
    public void testSetUserRightsTrue() throws Exception {
        boolean success = true;
        doTestSetUserRights(success);
    }

    private void doTestSetUserRights(boolean success)
        throws DBNotFoundException, DuracloudInstanceNotAvailableException, AccountNotFoundException {
        createPropagatorSetRights(success);
        createJointPointSetRights(success);
        aspect = new DuracloudUserServiceAspect(propagator);

        aspect.setUserRights(joinPoint, success);
    }

    private UserDetailsPropagator createPropagatorSetRights(boolean success)
        throws DBNotFoundException, DuracloudInstanceNotAvailableException, AccountNotFoundException {
        propagator = EasyMock.createMock("UserDetailsPropagator",
                                         UserDetailsPropagator.class);
        if (success) {
            Set<Role> roles = new HashSet<Role>();
            roles.add(role);
            propagator.propagateRights(acctId, userId, roles);
            EasyMock.expectLastCall();
        }

        EasyMock.replay(propagator);
        return propagator;
    }

    private JoinPoint createJointPointSetRights(boolean success) {
        joinPoint = EasyMock.createMock("JoinPoint", JoinPoint.class);
        if (success) {
            Object[] args = new Object[]{acctId, userId, new Role[]{role}};
            EasyMock.expect(joinPoint.getArgs()).andReturn(args);
        }

        EasyMock.replay(joinPoint);
        return joinPoint;
    }

    @Test
    public void testRevokeUserRights() throws Exception {
        createPropagatorRevokeRights();
        createJointPointRevokeRights();

        aspect = new DuracloudUserServiceAspect(propagator);
        aspect.revokeUserRights(joinPoint);
    }

    private UserDetailsPropagator createPropagatorRevokeRights()
        throws DBNotFoundException, DuracloudInstanceNotAvailableException, AccountNotFoundException {
        propagator = EasyMock.createMock("UserDetailsPropagator",
                                         UserDetailsPropagator.class);

        propagator.propagateRevocation(acctId, userId);
        EasyMock.expectLastCall();
        EasyMock.replay(propagator);
        return propagator;
    }

    private JoinPoint createJointPointRevokeRights() {
        joinPoint = EasyMock.createMock("JoinPoint", JoinPoint.class);

        Object[] args = new Object[]{acctId, userId};
        EasyMock.expect(joinPoint.getArgs()).andReturn(args);
        EasyMock.replay(joinPoint);
        return joinPoint;
    }
}
