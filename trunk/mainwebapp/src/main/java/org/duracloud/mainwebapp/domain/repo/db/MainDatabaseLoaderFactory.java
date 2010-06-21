/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo.db;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.mainwebapp.domain.repo.AddressRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.AuthorityRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.ComputeAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.ComputeProviderRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.CredentialRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.DuraCloudAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.StorageAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.StorageProviderRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.UserRepositoryDBImpl;
import org.duracloud.mainwebapp.mgmt.ComputeAcctManagerImpl;
import org.duracloud.mainwebapp.mgmt.CredentialManagerImpl;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManagerImpl;
import org.duracloud.mainwebapp.mgmt.StorageAcctManagerImpl;
import org.duracloud.mainwebapp.mgmt.UserManagerImpl;

public class MainDatabaseLoaderFactory {

    private final DuraCloudAcctManagerImpl duraCloudAcctManager;

    private final UserManagerImpl userManager;

    private final CredentialManagerImpl credentialManager;

    private final ComputeAcctManagerImpl computeAcctManager;

    private final StorageAcctManagerImpl storageAcctManager;

    private final AddressRepositoryDBImpl addressRepository =
            new AddressRepositoryDBImpl();

    private final UserRepositoryDBImpl userRepository =
            new UserRepositoryDBImpl();

    private final AuthorityRepositoryDBImpl authorityRepository =
            new AuthorityRepositoryDBImpl();

    private final CredentialRepositoryDBImpl credentialRepository =
            new CredentialRepositoryDBImpl();

    private final ComputeProviderRepositoryDBImpl computeProviderRepository =
            new ComputeProviderRepositoryDBImpl();

    private final ComputeAcctRepositoryDBImpl computeAcctRepository =
            new ComputeAcctRepositoryDBImpl();

    private final StorageProviderRepositoryDBImpl storageProviderRepository =
            new StorageProviderRepositoryDBImpl();

    private final StorageAcctRepositoryDBImpl storageAcctRepository =
            new StorageAcctRepositoryDBImpl();

    private final DuraCloudAcctRepositoryDBImpl duraCloudAcctRepository =
            new DuraCloudAcctRepositoryDBImpl();

    private final MainDatabaseLoader loader;

    public MainDatabaseLoaderFactory(DatabaseUtil dbUtil) {
        this(dbUtil, new Credential("username", "password"));
    }

    public MainDatabaseLoaderFactory(DatabaseUtil dbUtil, Credential amazonCred) {
        addressRepository.setDataSource(dbUtil.getDataSource());
        userRepository.setDataSource(dbUtil.getDataSource());
        authorityRepository.setDataSource(dbUtil.getDataSource());
        credentialRepository.setDataSource(dbUtil.getDataSource());
        computeProviderRepository.setDataSource(dbUtil.getDataSource());
        computeAcctRepository.setDataSource(dbUtil.getDataSource());
        storageProviderRepository.setDataSource(dbUtil.getDataSource());
        storageAcctRepository.setDataSource(dbUtil.getDataSource());
        duraCloudAcctRepository.setDataSource(dbUtil.getDataSource());

        duraCloudAcctManager = new DuraCloudAcctManagerImpl();
        userManager = new UserManagerImpl();
        credentialManager = new CredentialManagerImpl();
        computeAcctManager = new ComputeAcctManagerImpl();
        storageAcctManager = new StorageAcctManagerImpl();

        duraCloudAcctManager
                .setDuraCloudAcctRepository(duraCloudAcctRepository);
        duraCloudAcctManager.setUserManager(userManager);
        duraCloudAcctManager.setComputeAcctManager(computeAcctManager);
        duraCloudAcctManager.setStorageAcctManager(storageAcctManager);

        userManager.setUserRepository(userRepository);
        userManager.setCredentialManager(credentialManager);
        userManager.setAddressRepository(addressRepository);

        credentialManager.setCredentialRepository(credentialRepository);
        credentialManager.setAuthorityRepository(authorityRepository);

        computeAcctManager.setComputeAcctRepository(computeAcctRepository);
        computeAcctManager
                .setComputeProviderRepository(computeProviderRepository);
        computeAcctManager.setCredentialManager(credentialManager);
        computeAcctManager.setDuraCloudAcctManager(duraCloudAcctManager);

        storageAcctManager.setStorageAcctRepository(storageAcctRepository);
        storageAcctManager
                .setStorageProviderRepository(storageProviderRepository);
        storageAcctManager.setCredentialManager(credentialManager);

        loader = new MainDatabaseLoader(amazonCred);
        loader.setDuraCloudAcctManager(duraCloudAcctManager);
        loader.setComputeProviderRepository(computeProviderRepository);
        loader.setStorageProviderRepository(storageProviderRepository);
    }

    public MainDatabaseLoader getMainDatabaseLoader() {
        return loader;
    }

}
