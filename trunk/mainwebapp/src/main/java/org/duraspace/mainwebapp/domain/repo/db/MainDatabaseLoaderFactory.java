
package org.duraspace.mainwebapp.domain.repo.db;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.repo.AddressRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.AuthorityRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.CredentialRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.DuraSpaceAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.StorageAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.StorageProviderRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.UserRepositoryDBImpl;
import org.duraspace.mainwebapp.mgmt.ComputeAcctManagerImpl;
import org.duraspace.mainwebapp.mgmt.CredentialManagerImpl;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManagerImpl;
import org.duraspace.mainwebapp.mgmt.StorageAcctManagerImpl;
import org.duraspace.mainwebapp.mgmt.UserManagerImpl;

public class MainDatabaseLoaderFactory {

    private final DuraSpaceAcctManagerImpl duraSpaceAcctManager;

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

    private final DuraSpaceAcctRepositoryDBImpl duraSpaceAcctRepository =
            new DuraSpaceAcctRepositoryDBImpl();

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
        duraSpaceAcctRepository.setDataSource(dbUtil.getDataSource());

        duraSpaceAcctManager = new DuraSpaceAcctManagerImpl();
        userManager = new UserManagerImpl();
        credentialManager = new CredentialManagerImpl();
        computeAcctManager = new ComputeAcctManagerImpl();
        storageAcctManager = new StorageAcctManagerImpl();

        duraSpaceAcctManager
                .setDuraSpaceAcctRepository(duraSpaceAcctRepository);
        duraSpaceAcctManager.setUserManager(userManager);
        duraSpaceAcctManager.setComputeAcctManager(computeAcctManager);
        duraSpaceAcctManager.setStorageAcctManager(storageAcctManager);

        userManager.setUserRepository(userRepository);
        userManager.setCredentialManager(credentialManager);
        userManager.setAddressRepository(addressRepository);

        credentialManager.setCredentialRepository(credentialRepository);
        credentialManager.setAuthorityRepository(authorityRepository);

        computeAcctManager.setComputeAcctRepository(computeAcctRepository);
        computeAcctManager
                .setComputeProviderRepository(computeProviderRepository);
        computeAcctManager.setCredentialManager(credentialManager);
        computeAcctManager.setDuraSpaceAcctManager(duraSpaceAcctManager);

        storageAcctManager.setStorageAcctRepository(storageAcctRepository);
        storageAcctManager
                .setStorageProviderRepository(storageProviderRepository);
        storageAcctManager.setCredentialManager(credentialManager);

        loader = new MainDatabaseLoader(amazonCred);
        loader.setDuraSpaceAcctManager(duraSpaceAcctManager);
        loader.setComputeProviderRepository(computeProviderRepository);
        loader.setStorageProviderRepository(storageProviderRepository);
    }

    public MainDatabaseLoader getMainDatabaseLoader() {
        return loader;
    }

}
