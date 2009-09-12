
package org.duracloud.durastore.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.duracloud.common.model.Credential;
import org.duracloud.emcstorage.EMCStorageProvider;
import org.duracloud.rackspacestorage.RackspaceStorageProvider;
import org.duracloud.s3storage.S3StorageProvider;
import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duracloud.storage.provider.StorageProvider;

import static org.junit.Assert.assertNotNull;

/**
 * This class runs a suite of tests on the StorageProvider interface across all
 * support provider implementations.
 *
 * @author Andrew Woods
 */
public class StorageProvidersTest {

    protected final static Logger log =
            Logger.getLogger(StorageProvidersTest.class);

    private final static StorageProvidersTestInterface tester =
            new StorageProvidersTestProxyPipe();

    private final static List<StorageProvider> storageProviders =
            new ArrayList<StorageProvider>();

    private final List<String> spaceIds = new ArrayList<String>();

    @BeforeClass
    public static void beforeClass() throws StorageException {

        final int NUM_PROVIDERS = 3;
        for (StorageProviderType providerType : StorageProviderType.values()) {
            Credential credential = getCredential(providerType);
            if (credential != null) {
                String user = credential.getUsername();
                String pass = credential.getPassword();

                StorageProvider provider = null;
                if (StorageProviderType.AMAZON_S3.equals(providerType)) {
                    provider = new S3StorageProvider(user, pass);
                } else if (StorageProviderType.EMC.equals(providerType)) {
                    provider = new EMCStorageProvider(user, pass);
                } else if (StorageProviderType.RACKSPACE.equals(providerType)) {
                    provider = new RackspaceStorageProvider(user, pass);
                } else {
                    StringBuffer sb = new StringBuffer("NOT TESTING ");
                    sb.append("storage-provider: '" + providerType + "'");
                    log.info(sb.toString());
                }

                if (provider != null) {
                    storageProviders.add(provider);
                }
            }
        }

        Assert.assertEquals(NUM_PROVIDERS, storageProviders.size());
    }

    private static Credential getCredential(StorageProviderType type) {
        log.debug("Getting credential for: '" + type + "'");

        Credential credential = null;
        try {
            UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
            credential = dbUtil.findCredentialForProvider(type);
            assertNotNull(credential);

            String username = credential.getUsername();
            String password = credential.getPassword();
            assertNotNull(username);
            assertNotNull(password);
        } catch (Exception e) {
            log.warn("Error getting credential for: '" + type + "'");
        }

        return credential;
    }

    @Before
    public void setUp() {
        try {
            clean();
        } catch (Exception e) {
            log.info(e);
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            clean();
        } catch (Exception e) {
            log.info(e);
        }
    }

    @AfterClass
    public static void afterClass() {
        tester.close();
    }

    private void clean() {
        for (StorageProvider provider : storageProviders) {
            assertNotNull(provider);

            for (String spaceId : spaceIds) {
                deleteSpace(provider, spaceId);
            }
        }
    }

    private void deleteSpace(StorageProvider provider, String spaceId) {
        try {
            provider.deleteSpace(spaceId);
        } catch (Exception e) {
        }
    }

    private String getNewSpaceId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String spaceId = "duracloud-test-space." + random;
        spaceIds.add(spaceId);
        return spaceId;
    }

    private String getNewContentId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String contentId = "duracloud-test-content." + random;
        return contentId;
    }

    @Test
    public void testGetSpaces() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();
        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaces(provider, spaceId0, spaceId1);
        }
    }

    @Test
    public void testGetSpaceContents() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceContents(provider,
                                        spaceId0,
                                        contentId0,
                                        contentId1);
        }
    }

    // TODO: not all work
    @Test
    public void testCreateSpace() throws StorageException {
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            if (!S3StorageProvider.class.isInstance(provider)
                    && !RackspaceStorageProvider.class.isInstance(provider)) {
                tester.testCreateSpace(provider, spaceId0);
            }
        }
    }

    // TODO: not all work
    @Test
    public void testDeleteSpace() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            if (!S3StorageProvider.class.isInstance(provider)
                    && !RackspaceStorageProvider.class.isInstance(provider)
                    && !EMCStorageProvider.class.isInstance(provider)) {
                tester.testDeleteSpace(provider, spaceId0, spaceId1);
            }
        }
    }

    @Test
    public void testGetSpaceMetadata() throws StorageException {
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceMetadata(provider, spaceId0);
        }
    }

    // TODO: not all work
    @Test
    public void testSetSpaceMetadata() throws StorageException {
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            if (!EMCStorageProvider.class.isInstance(provider)
                    && !RackspaceStorageProvider.class.isInstance(provider)) {
                tester.testSetSpaceMetadata(provider, spaceId0);
            }
        }
    }

    @Test
    public void testGetSpaceAccess() throws StorageException {
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceAccess(provider, spaceId0);
        }
    }

    @Test
    public void testAddAndGetContent() throws Exception {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();
        String contentId2 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testAddAndGetContent(provider,
                                        spaceId0,
                                        contentId0,
                                        contentId1,
                                        contentId2);
        }
    }

    // TODO: not all work
    @Test
    public void testAddAndGetContentOverwrite() throws Exception {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!EMCStorageProvider.class.isInstance(provider)) {
                tester.testAddAndGetContentOverwrite(provider,
                                                     spaceId0,
                                                     contentId0,
                                                     contentId1);
            }
        }
    }

    @Test
    public void testAddContentLarge() throws Exception {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testAddContentLarge(provider,
                                       spaceId0,
                                       contentId0,
                                       contentId1);
        }
    }

    // TODO: not all work
    @Test
    public void testDeleteContent() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!S3StorageProvider.class.isInstance(provider)
                    && !EMCStorageProvider.class.isInstance(provider)
                    && !RackspaceStorageProvider.class.isInstance(provider)) {
                tester.testDeleteContent(provider,
                                         spaceId0,
                                         contentId0,
                                         contentId1);
            }
        }
    }

    // TODO: not all work
    @Test
    public void testSetContentMetadata() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();
        String contentId0 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!RackspaceStorageProvider.class.isInstance(provider)) {
                tester.testSetContentMetadata(provider,
                                              spaceId0,
                                              spaceId1,
                                              contentId0);
            }
        }
    }

    // TODO: not all work
    @Test
    public void testGetContentMetadata() throws StorageException {
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!S3StorageProvider.class.isInstance(provider)
                    && !EMCStorageProvider.class.isInstance(provider)
                    && !RackspaceStorageProvider.class.isInstance(provider)) {
                tester.testGetContentMetadata(provider, spaceId0, contentId0);
            }
        }

    }

    // TODO: need to implement
    @Test
    public void testSpaceAccess() throws Exception {

    }

    // TODO: need to implement
    @Test
    public void testContentAccess() throws Exception {

    }

}
