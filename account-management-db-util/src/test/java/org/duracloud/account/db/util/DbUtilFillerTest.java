/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Console;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author: Bill Branan
 * Date: 4/13/11
 */
public class DbUtilFillerTest {

    private TestDbUtilFiller filler;
    private DuracloudRepoMgr repoMgr;
    private DuracloudAccountRepo acctRepo;
    private DuracloudStorageProviderAccountRepo storageRepo;
    private DuracloudComputeProviderAccountRepo computeRepo;
    private Console console;
    private String subdomain = "yes";
    private String newValue = "new-value";

    @Before
    public void setup() {
        acctRepo = EasyMock.createMock(DuracloudAccountRepo.class);
        storageRepo =
            EasyMock.createMock(DuracloudStorageProviderAccountRepo.class);
        computeRepo =
            EasyMock.createMock(DuracloudComputeProviderAccountRepo.class);
        repoMgr = EasyMock.createMock(DuracloudRepoMgr.class);
        filler = new TestDbUtilFiller(repoMgr);
    }

    private class TestDbUtilFiller extends DbUtilFiller {
        public TestDbUtilFiller(DuracloudRepoMgr repoMgr) {
            super(repoMgr, null);
        }

        @Override
        protected String getInput(String display, String origValue) {
            return newValue;
        }

        @Override
        protected String readInput() {
            return subdomain;
        }
    }

    private void replayMocks() {
        EasyMock.replay(repoMgr, acctRepo, storageRepo, computeRepo);
    }

    @After
    public void tearDown() {
        EasyMock.verify(repoMgr, acctRepo, storageRepo, computeRepo);
    }

    @Test
    public void testFill() throws Exception {
        int acctId = 0;
        int computeAcctId = 10;
        int primStorageAcctId = 20;
        int secStorageAcctId = 21;
        Set<Integer> secStorageAcctIds = new HashSet<Integer>();
        secStorageAcctIds.add(secStorageAcctId);
        int paymentId = 30;
        AccountInfo.PackageType packageType =
            AccountInfo.PackageType.PROFESSIONAL;
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.PENDING;
        String tbd = "TBD";

        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(acctRepo)
            .times(1);
        EasyMock.expect(repoMgr.getStorageProviderAccountRepo())
            .andReturn(storageRepo)
            .times(1);
        EasyMock.expect(repoMgr.getComputeProviderAccountRepo())
            .andReturn(computeRepo)
            .times(1);

        AccountInfo account =
            new AccountInfo(acctId, subdomain, "acctName", "orgName", "dept",
                            computeAcctId, primStorageAcctId, secStorageAcctIds,
                            null, paymentId, packageType, status);
        EasyMock.expect(acctRepo.findBySubdomain(EasyMock.isA(String.class)))
            .andReturn(account)
            .times(1);

        StorageProviderAccount primStorageAcct =
            new StorageProviderAccount(primStorageAcctId,
                                       StorageProviderType.AMAZON_S3,
                                       tbd, tbd, false);
        EasyMock.expect(storageRepo.findById(EasyMock.anyInt()))
            .andReturn(primStorageAcct);

        StorageProviderAccount secStorageAcct =
            new StorageProviderAccount(secStorageAcctId,
                                       StorageProviderType.RACKSPACE,
                                       tbd, tbd, false);
        EasyMock.expect(storageRepo.findById(EasyMock.anyInt()))
            .andReturn(secStorageAcct);

        Capture<StorageProviderAccount> primStorageCapture =
            new Capture<StorageProviderAccount>();
        storageRepo.save(EasyMock.capture(primStorageCapture));
        EasyMock.expectLastCall();

        Capture<StorageProviderAccount> secStorageCapture =
            new Capture<StorageProviderAccount>();
        storageRepo.save(EasyMock.capture(secStorageCapture));
        EasyMock.expectLastCall();

        ComputeProviderAccount computeAcct =
            new ComputeProviderAccount(computeAcctId,
                                       ComputeProviderType.AMAZON_EC2,
                                       tbd, tbd, tbd, tbd, tbd);
        EasyMock.expect(computeRepo.findById(EasyMock.anyInt()))
            .andReturn(computeAcct);

        Capture<ComputeProviderAccount> computeCapture =
            new Capture<ComputeProviderAccount>();
        computeRepo.save(EasyMock.capture(computeCapture));
        EasyMock.expectLastCall();

        Capture<AccountInfo> accountCapture = new Capture<AccountInfo>();
        acctRepo.save(EasyMock.capture(accountCapture));
        EasyMock.expectLastCall();

        replayMocks();

        filler.fill();

        StorageProviderAccount primStorageResult = primStorageCapture.getValue();
        assertEquals(primStorageAcctId, primStorageResult.getId());
        assertEquals(newValue, primStorageResult.getUsername());
        assertEquals(newValue, primStorageResult.getPassword());

        StorageProviderAccount secStorageResult = secStorageCapture.getValue();
        assertEquals(secStorageAcctId, secStorageResult.getId());
        assertEquals(newValue, secStorageResult.getUsername());
        assertEquals(newValue, secStorageResult.getPassword());

        ComputeProviderAccount computeResult = computeCapture.getValue();
        assertEquals(computeAcctId, computeResult.getId());
        assertEquals(newValue, computeResult.getUsername());
        assertEquals(newValue, computeResult.getPassword());
        assertEquals(newValue, computeResult.getElasticIp());
        assertEquals(newValue, computeResult.getKeypair());
        assertEquals(newValue, computeResult.getSecurityGroup());

        AccountInfo accountResult = accountCapture.getValue();
        assertEquals(AccountInfo.AccountStatus.ACTIVE,
                     accountResult.getStatus());
    }

}
