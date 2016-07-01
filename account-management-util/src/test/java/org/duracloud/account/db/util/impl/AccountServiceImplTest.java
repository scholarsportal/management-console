/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.config.AmaEndpoint;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @author Daniel Bernstein
 *
 */
@RunWith(EasyMockRunner.class)
public class AccountServiceImplTest extends EasyMockSupport{

    @After
    public void tearDown(){
        verifyAll();
    }

    @Test
    public void testChangePrimaryStorageProvider () {
        DuracloudRepoMgr repoMgr = createMock(DuracloudRepoMgr.class);
        AmaEndpoint amaEndpoint = createMock(AmaEndpoint.class);
        AccountInfo acct = createMock(AccountInfo.class);
        DuracloudAccountRepo accountRepo = createMock(DuracloudAccountRepo.class);
        expect(repoMgr.getAccountRepo()).andReturn(accountRepo);
        expect(accountRepo.save(acct)).andReturn(acct);
        Long storageProviderId = 1l;
        StorageProviderAccount primary = createStorageProviderAccount(3l, StorageProviderType.AMAZON_S3);
        StorageProviderAccount secondary = createStorageProviderAccount(
                storageProviderId,  StorageProviderType.SNAPSHOT);
        Set<StorageProviderAccount> secondaryaccounts = new HashSet<>();
        secondaryaccounts.add( secondary);

        Set<StorageProviderAccount> result = new HashSet<>();
        result.add(primary);

        expect(acct.getSecondaryStorageProviderAccounts()).andReturn(secondaryaccounts);
        expect(acct.getPrimaryStorageProviderAccount()).andReturn(primary);
        acct.setPrimaryStorageProviderAccount(secondary);
        acct.setSecondaryStorageProviderAccounts(eq(result));
        expectLastCall();
        
        expect(acct.getSubdomain()).andReturn("test");
        
        replayAll();
        AccountService service = new AccountServiceImpl(amaEndpoint, acct, repoMgr);

        service.changePrimaryStorageProvider(storageProviderId);
    }

    private StorageProviderAccount createStorageProviderAccount(
            Long storageProviderId, StorageProviderType providerType) {
        StorageProviderAccount account = new StorageProviderAccount();
        account.setId(storageProviderId);
        account.setProviderType(providerType);
        return account;
    }

}
