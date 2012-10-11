/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.error.DuracloudProviderAccountNotAvailableException;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.notification.Emailer;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods Date: Dec 10, 2010
 */
public class AccountServiceImplTest extends DuracloudServiceTestBase {

    private AccountServiceImpl acctService;
    private AccountInfo acctInfo;

    private final int acctId = 1;
    private final String adminUsername = "username";
    private final String subdomain = "sub-domain";
    private Set<StorageProviderType> storageProviders;

    @Before
    public void before() throws Exception {
        super.before();

        storageProviders = new HashSet<StorageProviderType>();
        for (StorageProviderType provider : StorageProviderType.values()) {
            storageProviders.add(provider);
        }

        acctInfo = newAccountInfo(acctId, subdomain);
        acctService =
            new AccountServiceImpl(acctInfo, repoMgr, providerAccountUtil);

         EasyMock.expect(serverDetailsRepo.findById(EasyMock.anyInt()))
            .andReturn(newServerDetails(0))
            .anyTimes();
    }

    @Test
    public void testRetrieveServerDetails() throws Exception {
        replayMocks();

        ServerDetails serverDetails = acctService.retrieveServerDetails();
        Assert.assertNotNull(serverDetails);
    }

    @Test
    public void testStoreServerDetails() throws Exception {
        int serverDetailsId = 12;

        serverDetailsRepo.save(EasyMock.isA(ServerDetails.class));
        EasyMock.expectLastCall()
           .times(1);

        replayMocks();

        ServerDetails serverDetails = newServerDetails(serverDetailsId);
        acctService.storeServerDetails(serverDetails);
    }

    @Test
    public void testGetPrimaryStorageProvider() throws Exception {
        StorageProviderAccount storageAcct = setUpGetStorageProviders();
        StorageProviderAccount retVal = acctService.getPrimaryStorageProvider();
        Assert.assertNotNull(retVal);
        Assert.assertEquals(storageAcct, retVal);
    }

    
    @Test
    public void testGetComputeProvider() throws Exception {
        setUpGetComputeProvider();
        ComputeProviderAccount retVal = acctService.getComputeProvider();
        Assert.assertNotNull(retVal);
    }

    @Test
    public void testGetSecondaryStorageProviders() throws Exception {
        StorageProviderAccount storageAcct = setUpGetStorageProviders();
        Set<StorageProviderAccount> retVals = acctService.getSecondaryStorageProviders();
        Assert.assertNotNull(retVals);
        Assert.assertEquals(1, retVals.size());
        Assert.assertEquals(storageAcct, retVals.iterator().next());
    }

    private StorageProviderAccount setUpGetStorageProviders() throws Exception {
        StorageProviderAccount storageAcct =
            new StorageProviderAccount(1, StorageProviderType.AMAZON_S3,
                                       "username", "password", false);
        EasyMock.expect(storageProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(storageAcct)
            .times(1);

        replayMocks();

        return storageAcct;
    }

    private ComputeProviderAccount setUpGetComputeProvider() throws Exception {
        ComputeProviderAccount compute =
            new ComputeProviderAccount(1,
                                       ComputeProviderType.AMAZON_EC2,
                                       "username",
                                       "password",
                                       "ip",
                                       "security-group",
                                       "keypair");
        EasyMock.expect(computeProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(compute)
            .times(1);

        replayMocks();

        return compute;
    }

    @Test
    public void testAddStorageProvider() throws Exception {
        StorageProviderType typeToAdd = StorageProviderType.MICROSOFT_AZURE;

        EasyMock.expect(providerAccountUtil.
            createEmptyStorageProviderAccount(typeToAdd))
            .andReturn(0)
            .times(1);

        serverDetailsRepo.save(EasyMock.isA(ServerDetails.class));
        EasyMock.expectLastCall()
           .times(1);

        replayMocks();

        acctService.addStorageProvider(typeToAdd);
    }

    @Test
    public void testSetPrimaryStorageProviderRrs() throws Exception {
        StorageProviderAccount storageAcct =
            new StorageProviderAccount(1, StorageProviderType.AMAZON_S3,
                                       "username", "password", false);
        EasyMock.expect(storageProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(storageAcct)
            .times(1);

        storageProviderAcctRepo.save(EasyMock.isA(StorageProviderAccount.class));
        EasyMock.expectLastCall()
            .times(1);

        replayMocks();

        acctService.setPrimaryStorageProviderRrs(true);
    }

    @Test
    public void testRemoveStorageProvider() throws Exception {
        serverDetailsRepo.save(EasyMock.isA(ServerDetails.class));
        EasyMock.expectLastCall()
           .times(1);
        
        storageProviderAcctRepo.delete(0);
        EasyMock.expectLastCall()
            .times(1);

        replayMocks();

        acctService.removeStorageProvider(0);

        // Item has been removed, should fail a subsequent request
        try {
            acctService.removeStorageProvider(0);
            Assert.fail("Exception expected");
        } catch(DuracloudProviderAccountNotAvailableException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testGetUsers() throws Exception {
        Set<AccountRights> rights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        roles.add(Role.ROLE_ADMIN);
        roles.add(Role.ROLE_USER);
        int userId = 1;
        rights.add(new AccountRights(1, acctId, userId, roles));
        EasyMock.expect(this.rightsRepo.findByAccountId(acctId)).andReturn(
            rights);
        EasyMock.expect(this.userRepo.findById(userId)).andReturn(
            newDuracloudUser(userId, "test"));
        EasyMock.expectLastCall();
        replayMocks();
        Set<DuracloudUser> users = this.acctService.getUsers();
        Assert.assertNotNull(users);
        Assert.assertTrue(users.size() == 1);
    }

    @Test
    public void testDeleteUserInvitation() throws Exception {
        this.invitationRepo.delete(1);
        EasyMock.expectLastCall();
        replayMocks();
        this.acctService.deleteUserInvitation(1);

    }

    @Test
    public void testCancelAccount() throws Exception {
        this.accountRepo.save(EasyMock.anyObject(AccountInfo.class));
        EasyMock.expectLastCall();

        Map<String, String> adminAddresses = new HashMap<String, String>();
        adminAddresses.put("ama.admin.0", "admin@duracloud.org");

        Emailer emailer = EasyMock.createMock("Emailer", Emailer.class);

        emailer.send(EasyMock.isA(String.class),
                     EasyMock.isA(String.class),
                     EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        EasyMock.replay(emailer);
        replayMocks();
        this.acctService.cancelAccount("test", emailer, adminAddresses.values());

    }

    @Test
    public void testGetPendingUserInvitations() throws Exception {
        Set<UserInvitation> userInvitations = new HashSet<UserInvitation>();
        userInvitations.add(createUserInvite());
        EasyMock.expect(this.invitationRepo.findByAccountId(acctId)).andReturn(
            userInvitations);
        replayMocks();
        Set<UserInvitation> invites = this.acctService.getPendingInvitations();
        Assert.assertTrue(invites.size() > 0);
    }

    @Test
    public void testGetExpiredPendingUserInvitations() throws Exception {
        Set<UserInvitation> userInvitations = new HashSet<UserInvitation>();
        userInvitations.add(createUserInvite());
        userInvitations.add(new UserInvitation(2,
                                               acctId,
                                               "",
                                               "",
                                               new Date(),
                                               new Date(1, 1, 1),
                                               "",
                                               0));

        EasyMock.expect(this.invitationRepo.findByAccountId(acctId)).andReturn(
            userInvitations);

        this.invitationRepo.delete(2);
        EasyMock.expectLastCall();

        replayMocks();
        
        Set<UserInvitation> invites = this.acctService.getPendingInvitations();
        Assert.assertTrue(invites.size() == 1);
    }

    private UserInvitation createUserInvite() {
        return new UserInvitation(1, acctId, adminUsername, "test@duracloud.org", 4, "xyz");
   }

    @Test
    public void testInviteUser() throws Exception {
        String email = "test@duracloud.org";
        EasyMock.expect(this.idUtil.newUserInvitationId()).andReturn(1);

        this.invitationRepo.save(EasyMock.isA(UserInvitation.class));
        EasyMock.expectLastCall();

        Emailer emailer = EasyMock.createMock("Emailer", Emailer.class);

        Capture<String> capturedBody = new Capture<String>();
        emailer.send(EasyMock.isA(String.class),
                     EasyMock.capture(capturedBody),
                     EasyMock.eq(email));
        EasyMock.expectLastCall();

        EasyMock.replay(emailer);
        replayMocks();

        UserInvitation ui = this.acctService.inviteUser(email, adminUsername, emailer);
        Assert.assertTrue(ui.getId() == 1);
        Assert.assertTrue(ui.getRedemptionCode().length() > 3);

        String body = capturedBody.getValue();
        Assert.assertNotNull(body);
        Assert.assertTrue("Email body !contain the redemption code: " + body,
                          body.contains(ui.getRedemptionCode()));

        EasyMock.verify(emailer);
    }

    @Test
    public void testStoreAccountStatus() throws Exception {
        this.accountRepo.save(EasyMock.anyObject(AccountInfo.class));
        EasyMock.expectLastCall();
        replayMocks();
        this.acctService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);
    }

    
    @Test
    public void testStoreRetrieveAccountInfo() throws Exception {
        // FIXME: implement
        replayMocks();
    }

    @Test
    public void testStoreRetrievePaymentInfo() throws Exception {
        // FIXME: implement
        replayMocks();
    }

    @Test
    public void testStoreSubdomain() throws Exception {
        // FIXME: implement
        replayMocks();
    }
}
