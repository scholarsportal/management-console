/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;
import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class DuracloudInstanceServiceImplTest
    extends DuracloudInstanceServiceTestBase {

    private final int NUM_USERS = 5;

    @Test
    public void testGetInstanceInfo() throws Exception {
        replayMocks();

        DuracloudInstance instanceInfo = service.getInstanceInfo();
        assertNotNull(instanceInfo);
        assertEquals(instance, instanceInfo);
    }

    @Test
    public void testGetStatus() throws Exception {
        String status = "status";
        EasyMock.expect(computeProvider.getStatus(EasyMock.isA(String.class)))
            .andReturn(status)
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);

        replayMocks();

        String resultStatus = service.getStatus();
        assertNotNull(resultStatus);
        assertEquals(status, resultStatus);
    }

    @Test
    public void testStop() throws Exception {
        computeProvider.stop(EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);

        replayMocks();

        service.stop();
    }

    @Test
    public void testRestart() throws Exception {
        computeProvider.restart(EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);
        EasyMock.expect(instance.getHostName())
            .andReturn("host")
            .times(4);
        EasyMock.expect(instance.getDcRootUsername())
            .andReturn("user")
            .times(2);
        EasyMock.expect(instance.getDcRootPassword())
            .andReturn("pass")
            .times(2);
        instanceUpdater.initializeInstance(EasyMock.isA(String.class),
                                           EasyMock.isA(DuradminConfig.class),
                                           EasyMock.isA(DurastoreConfig.class),
                                           EasyMock
                                               .isA(DuraserviceConfig.class),
                                           EasyMock.isA(RestHttpHelper.class));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.expect(repoMgr.getRightsRepo())
            .andReturn(rightsRepo)
            .times(1);
        EasyMock.expect(repoMgr.getUserRepo())
            .andReturn(userRepo)
            .times(1);

        int userId = 5;
        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        accountRights.add(new AccountRights(0, accountId, userId, roles));
        EasyMock.expect(rightsRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(accountRights)
            .times(1);

        DuracloudUser user =
            new DuracloudUser(userId, "user", "pass", "first", "last", "email");
        user.setAccountRights(accountRights);
        EasyMock.expect(userRepo.findById(userId))
            .andReturn(user)
            .times(1);

        instanceUpdater.updateUserDetails(EasyMock.isA(String.class),
                                          EasyMock.isA(Set.class),
                                          EasyMock.isA(RestHttpHelper.class));
        EasyMock.expectLastCall()
            .times(1);

        replayMocks();

        service.restart();
    }

    @Test
    public void testInitializeComputeProvider() throws Exception {
        setUpInitComputeProvider();
        replayMocks();

        service = new DuracloudInstanceServiceImpl(accountId,
                                                   instance,
                                                   repoMgr,
                                                   computeProviderUtil);
    }

    @Test
    public void testSetUserRoles() {
        Capture<Set<SecurityUserBean>> capture = new Capture<Set<SecurityUserBean>>();
        setUpUserRoleMocks(capture);
        Set<DuracloudUser> users = createUsers();

        // This is the call under test.
        service.setUserRoles(users);

        Set<SecurityUserBean> beans = capture.getValue();
        Assert.assertNotNull(beans);

        verifyRoles(users, beans);
    }

    private void setUpUserRoleMocks(Capture<Set<SecurityUserBean>> capturedUsers) {
        instance = createInstanceExpectations();
        instanceUpdater = createInstanceUpdaterExpectations(capturedUsers);

        replayMocks();
    }

    private DuracloudInstance createInstanceExpectations() {
        EasyMock.expect(instance.getDcRootUsername()).andReturn("username");
        EasyMock.expect(instance.getDcRootPassword()).andReturn("password");
        EasyMock.expect(instance.getHostName()).andReturn("hostname");
        return instance;
    }

    private InstanceUpdater createInstanceUpdaterExpectations(Capture<Set<SecurityUserBean>> capturedUsers) {
        instanceUpdater.updateUserDetails(EasyMock.isA(String.class),
                                          EasyMock.capture(capturedUsers),
                                          EasyMock.isA(RestHttpHelper.class));
        EasyMock.expectLastCall();
        return instanceUpdater;
    }

    private Set<DuracloudUser> createUsers() {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        for (int i = 0; i < NUM_USERS; ++i) {
            DuracloudUser user = newDuracloudUser(i, "user-" + i);

            Set<Role> roles = new HashSet<Role>();
            roles.add(getRole(i));

            AccountRights accountRight = new AccountRights(-1,
                                                           accountId,
                                                           i,
                                                           roles);
            Set<AccountRights> accountRights = new HashSet<AccountRights>();
            accountRights.add(accountRight);
            user.setAccountRights(accountRights);

            users.add(user);
        }
        return users;
    }

    private Role getRole(int i) {
        int count = 1;
        for (Role role : Role.values()) {
            if (i % count == 0) {
                return role;
            }
            count++;
        }
        Assert.fail("should not have executed this line.");
        return null;
    }

    private void verifyRoles(Set<DuracloudUser> users,
                             Set<SecurityUserBean> beans) {
        Assert.assertEquals(users.size(), beans.size());

        for (DuracloudUser user: users)
        {
            String username = user.getUsername();
            SecurityUserBean bean = getBeanWithName(username, beans);

            Set<Role> roles = user.getRolesByAcct(accountId);
            Assert.assertNotNull(roles);
            Assert.assertTrue(roles.size() > 0);

            int numFound = 0;
            List<String> auths = bean.getGrantedAuthorities();
            Assert.assertNotNull(username, auths);

            Assert.assertEquals(roles.size(), auths.size());
            for (Role role : roles) {
                for (String auth : auths) {
                    if (auth.equals(role.name())) {
                        numFound++;
                    }
                }
            }
            Assert.assertEquals("username", roles.size(), numFound);
        }
    }

    private SecurityUserBean getBeanWithName(String username,
                                             Set<SecurityUserBean> beans) {
        for (SecurityUserBean bean : beans) {
            if (username.equals(bean.getUsername())) {
                return bean;
            }
        }
        Assert.fail("username not found in beans: " + username);
        return null;
    }

    @Test
    public void testSetUserRolesEmpty() throws Exception {
        replayMocks();
        
        doTestSetUserRoles(new HashSet<DuracloudUser>());
        doTestSetUserRoles(null);
    }

    private void doTestSetUserRoles(HashSet<DuracloudUser> users) {
        boolean thrown = false;
        try {
            service.setUserRoles(users);
            Assert.fail("exception expected");

        } catch (DuracloudInstanceUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

}
