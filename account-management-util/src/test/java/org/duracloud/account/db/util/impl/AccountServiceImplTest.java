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
import org.duracloud.account.db.model.ServerDetails;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudServerDetailsRepo;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.impl.StorageAccountImpl;
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
        ServerDetails details = createMock(ServerDetails.class);
        Long storageProviderId = 1l;
        
        StorageProviderAccount primary = createStorageProviderAccount(3l, StorageProviderType.AMAZON_S3);
        StorageProviderAccount secondary = createStorageProviderAccount(
                storageProviderId,  StorageProviderType.SNAPSHOT);
        Set<StorageProviderAccount> secondaryaccounts = new HashSet<>();
        secondaryaccounts.add( secondary);

        Set<StorageProviderAccount> result = new HashSet<>();
        result.add(primary);

        expect(details.getSecondaryStorageProviderAccounts()).andReturn(secondaryaccounts);
        expect(details.getPrimaryStorageProviderAccount()).andReturn(primary);
        details.setPrimaryStorageProviderAccount(secondary);
        details.setSecondaryStorageProviderAccounts(eq(result));
        expectLastCall();
        
        expect(acct.getSubdomain()).andReturn("test");
        expect(acct.getServerDetails()).andReturn(details);
        
        DuracloudServerDetailsRepo serverDetailsRepo = createMock(DuracloudServerDetailsRepo.class);
        expect(repoMgr.getServerDetailsRepo()).andReturn(serverDetailsRepo);
        expect(serverDetailsRepo.save(details)).andReturn(details);
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
